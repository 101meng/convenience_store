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
 * Order service: reads order history and submits new orders.
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderVO> getUserOrders(Integer userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("order_id");

        List<Order> orders = this.list(queryWrapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
        List<OrderVO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderVO vo = new OrderVO();
            vo.setOrderId(order.getOrderId());
            vo.setOrderSn(order.getOrderSn());
            vo.setActualAmount(order.getActualAmount());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitReq req) {
        List<CartVO> cartItems = cartMapper.getCartItemsWithProductInfo(req.getUserId(), req.getStoreId());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double subtotal = 0.0;
        for (CartVO item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double deliveryFee = req.getDeliveryFee() != null ? req.getDeliveryFee() : 0.0;
        double actualAmount = subtotal + deliveryFee;
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
        order.setStatus("completed");
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setCreatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        for (CartVO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(cartItem.getPrice());
            orderItemMapper.insert(orderItem);
        }

        QueryWrapper<Cart> deleteCartWrapper = new QueryWrapper<>();
        deleteCartWrapper.eq("user_id", req.getUserId());
        cartMapper.delete(deleteCartWrapper);

        return orderSn;
    }
}
