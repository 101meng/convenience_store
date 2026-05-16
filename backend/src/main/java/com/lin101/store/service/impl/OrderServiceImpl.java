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
 * 订单服务：列表查询委托父�?{@link ServiceImpl}；下单在同一事务内插入主表与明细并清空用户购物车�?
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    /** 下单前读取购物车联表结果、下单后�?user_id 清空购物�?*/
    @Autowired
    private CartMapper cartMapper;

    /** 插入主单、联查明细（{@link OrderMapper#getOrderItemsWithProductInfo}�?*/
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 用户订单列表：主表按 {@code order_id} 倒序；每条订单拉取明细（含商品名与图片）�?
     *
     * @param userId 当前登录用户
     * @return �?App 约定的英文月份时间字符串；无创建时间时用占位文案
     */
    @Override
    public List<OrderVO> getUserOrders(Integer userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("order_id");

        List<Order> orders = this.list(queryWrapper);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

        List<OrderVO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderVO vo = new OrderVO();
            // OrderItemVO：自定义 SQL 关联 products，带出名称与图片

            vo.setOrderId(order.getOrderId());
            vo.setOrderSn(order.getOrderSn());
            vo.setActualAmount(order.getActualAmount());

            // 与前端枚举比对统一用大�?
            vo.setStatus(order.getStatus().toUpperCase());
            vo.setOrderType(order.getOrderType());

            if (order.getCreatedAt() != null) {
                vo.setCreatedAt(order.getCreatedAt().format(formatter));
            } else {
                vo.setCreatedAt("Just now");
            }

            vo.setItems(orderMapper.getOrderItemsWithProductInfo(order.getOrderId()));

            result.add(vo);
        }

        return result;
    }

    /**
     * 提交订单：校验购物车非空 �?服务端重算金�?�?写主�?�?写明�?�?删除该用户购物车�?
     *
     * @param req 结算页提交的门店、运费、地址等（金额以本方法重算为准�?
     * @return 生成的业务单�?{@code ORD-2026-xxxxxxxx}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitReq req) {
        List<CartVO> cartItems = cartMapper.getCartItemsWithProductInfo(req.getUserId(), req.getStoreId());

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // 小计：单价取自购物车联表时的商品现价 × 数量
        double subtotal = 0.0;
        for (CartVO item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double deliveryFee = req.getDeliveryFee() != null ? req.getDeliveryFee() : 0.0;
        double actualAmount = subtotal + deliveryFee;

        // 业务单号：前缀 + 8 位随机hex（碰撞概率在毕设规模可忽略）
        String orderSn = "ORD-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setUserId(req.getUserId());
        order.setStoreId(req.getStoreId());
        order.setTotalAmount(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setActualAmount(actualAmount);
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setOrderType(req.getOrderType());
        order.setPaymentMethod(req.getPaymentMethod());
        // 无履约队列：演示直接 completed；真实业务可�?pending 再由后台 processOrder 推进
        order.setStatus("completed");
        // 与上�?setDeliveryAddress 重复赋值一次，属冗余写法，语义仍是同一地址
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setCreatedAt(LocalDateTime.now());

        // insert 后主键回填至 order.orderId，供明细外键使用
        orderMapper.insert(order);

        for (CartVO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            // 明细价快照，后续改商品价不影响历史订�?
            orderItem.setPriceAtTime(cartItem.getPrice());

            orderItemMapper.insert(orderItem);
        }

        // 下单成功后清空购物车，避免重复提�?
        QueryWrapper<Cart> deleteCartWrapper = new QueryWrapper<>();
        deleteCartWrapper.eq("user_id", req.getUserId());
        cartMapper.delete(deleteCartWrapper);

        return orderSn;
    }
}