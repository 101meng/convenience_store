package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Cart;
import com.lin101.store.entity.Order;
import com.lin101.store.entity.OrderItem;
import com.lin101.store.mapper.CartMapper;
import com.lin101.store.mapper.OrderItemMapper;
import com.lin101.store.mapper.OrderMapper;
import com.lin101.store.service.OrderService;
import com.lin101.store.vo.CartVO;
import com.lin101.store.vo.OrderSubmitReq;
import com.lin101.store.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 订单业务具体实现类
 * 继承 MyBatis-Plus 的 ServiceImpl 以获取基础 CRUD 功能
 * 实现 OrderService 接口以提供自定义业务逻辑
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    // 注入购物车数据访问层，用于查询用户购物车数据并清空购物车
    @Autowired
    private CartMapper cartMapper;

    // 注入订单主表数据访问层，用于订单查询和插入
    @Autowired
    private OrderMapper orderMapper;

    // 注入订单明细数据访问层，用于订单子项的插入
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 获取指定用户的所有历史订单列表，并组装好包含商品明细的完整视图对象
     *
     * @param userId 用户的唯一标识
     * @return 包含订单基本信息和商品明细列表的视图对象集合
     */
    @Override
    public List<OrderVO> getUserOrders(Integer userId) {
        // 构造查询条件：根据 user_id 精确匹配，并按 order_id 倒序排列（即最新订单在最前面）
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("order_id");

        // 执行数据库查询，获取订单主表记录列表
        List<Order> orders = this.list(queryWrapper);

        // 定义与前端 UI 设计图严格一致的国际化时间格式器 (例如: 15 Oct 2026, 10:30 AM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

        // 初始化用于返回给前端的最终结果列表
        List<OrderVO> result = new ArrayList<>();

        // 遍历所有订单记录，将实体类转换为视图对象 (VO)
        for (Order order : orders) {
            OrderVO vo = new OrderVO();

            // 填充订单基础信息
            vo.setOrderId(order.getOrderId());
            vo.setOrderSn(order.getOrderSn());
            vo.setActualAmount(order.getActualAmount());

            // 将状态转换为大写，以确保前端基于字符串比对时的一致性
            vo.setStatus(order.getStatus().toUpperCase());
            vo.setOrderType(order.getOrderType());

            // 格式化并填充真实的订单创建时间
            if (order.getCreatedAt() != null) {
                vo.setCreatedAt(order.getCreatedAt().format(formatter));
            } else {
                // 如果由于历史脏数据导致时间为空，则提供默认的占位提示
                vo.setCreatedAt("Just now");
            }

            // 调用 Mapper 中的自定义联表 SQL，查询并填充该订单下的所有商品明细
            vo.setItems(orderMapper.getOrderItemsWithProductInfo(order.getOrderId()));

            // 将组装完毕的视图对象加入结果集
            result.add(vo);
        }

        return result;
    }

    /**
     * 提交订单的完整业务流程，包含跨多表的复杂数据操作
     * 使用 @Transactional 注解确保事务的一致性，防止出现购物车被清空但订单未生成等数据异常
     *
     * @param req 前端传递的下单请求参数（包含门店、金额、地址等）
     * @return 下单成功后生成的全局唯一订单流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitReq req) {
        // 第一步：获取用户当前购物车中的所有商品详细信息（包括商品单价和数量）
        List<CartVO> cartItems = cartMapper.getCartItemsWithProductInfo(req.getUserId());

        // 严谨性校验：如果购物车为空，抛出异常阻断提交流程
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // 第二步：在服务端重新计算商品总金额，防止恶意篡改前端请求
        double subtotal = 0.0;
        for (CartVO item : cartItems) {
            // 单个商品的累加金额 = 当时单价 * 购买数量
            subtotal += item.getPrice() * item.getQuantity();
        }

        // 第三步：计算最终支付的实际总金额 = 商品总额 + 配送费/自提费
        double deliveryFee = req.getDeliveryFee() != null ? req.getDeliveryFee() : 0.0;
        double actualAmount = subtotal + deliveryFee;

        // 第四步：生成全局唯一的订单流水号，格式例如 ORD-2026-A1B2C3D4
        String orderSn = "ORD-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 第五步：构建订单主表实体对象并持久化到数据库
        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setUserId(req.getUserId());
        order.setStoreId(req.getStoreId());
        order.setTotalAmount(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setActualAmount(actualAmount);
        order.setOrderType(req.getOrderType());
        order.setPaymentMethod(req.getPaymentMethod());
        // 为了业务演示闭环，下单后直接将初始状态设为已完成
        order.setStatus("completed");
        order.setDeliveryAddress(req.getDeliveryAddress());
        // 写入当前系统真实时间作为订单创建时间
        order.setCreatedAt(LocalDateTime.now());

        // 执行插入操作，MyBatis-Plus 会自动将生成的自增主键回写到 order.orderId 中
        orderMapper.insert(order);

        // 第六步：遍历购物车中的商品，将其逐一转为订单明细项并持久化
        for (CartVO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            // 关联刚才生成的订单主键 ID
            orderItem.setOrderId(order.getOrderId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            // 记录下单时刻的商品快照单价，防止未来商品调价影响历史账单
            orderItem.setPriceAtTime(cartItem.getPrice());

            // 插入明细表
            orderItemMapper.insert(orderItem);
        }

        // 第七步：业务收尾，彻底清空该用户的购物车记录
        QueryWrapper<Cart> deleteCartWrapper = new QueryWrapper<>();
        deleteCartWrapper.eq("user_id", req.getUserId());
        cartMapper.delete(deleteCartWrapper);

        // 第八步：将生成的订单号返回给控制器，进而返回给前端展示
        return orderSn;
    }
}