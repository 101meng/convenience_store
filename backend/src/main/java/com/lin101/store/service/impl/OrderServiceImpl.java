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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 订单业务具体实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 提交订单的完整流程
     * @Transactional 注解极其重要：它保证了下面所有的数据库操作要么全部成功，要么全部回滚失败。
     * 防止出现“购物车清空了但订单没生成”的灾难级 Bug。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitReq req) {
        // 1. 获取用户当前购物车中的所有商品详细信息 (复用我们之前写好的 Mapper 方法)
        List<CartVO> cartItems = cartMapper.getCartItemsWithProductInfo(req.getUserId());

        // 如果购物车为空，抛出异常阻断流程
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // 2. 在后端重新计算商品总价 (防小人：永远不要完全信任前端传来的商品价格)
        double subtotal = 0.0;
        for (CartVO item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        // 3. 计算最终支付金额 = 商品总价 + 运费
        double deliveryFee = req.getDeliveryFee() != null ? req.getDeliveryFee() : 0.0;
        double actualAmount = subtotal + deliveryFee;

        // 4. 生成独特的订单流水号 (示例：ORD-2026-随机字符串)
        String orderSn = "ORD-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 5. 创建 Order 主表记录并插入数据库
        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setUserId(req.getUserId());
        order.setStoreId(req.getStoreId());
        order.setTotalAmount(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setActualAmount(actualAmount);
        order.setOrderType(req.getOrderType());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setStatus("completed"); // 为了毕设演示方便，下单直接标记为已完成
        order.setDeliveryAddress(req.getDeliveryAddress());

        // 插入主表，MyBatisPlus 会自动把生成的 orderId 赋给 order 对象
        orderMapper.insert(order);

        // 6. 遍历购物车，将商品逐一插入 OrderItem 明细表
        for (CartVO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId()); // 关联刚才生成的订单 ID
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(cartItem.getPrice()); // 记录当时的快照价格

            orderItemMapper.insert(orderItem);
        }

        // 7. 核心收尾：清空该用户的购物车
        QueryWrapper<Cart> deleteCartWrapper = new QueryWrapper<>();
        deleteCartWrapper.eq("user_id", req.getUserId());
        cartMapper.delete(deleteCartWrapper);

        // 8. 返回生成的订单号给前端展示
        return orderSn;
    }
}