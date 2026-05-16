package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Order;
import com.lin101.store.vo.OrderSubmitReq;
import com.lin101.store.vo.OrderVO;

import java.util.List;

/**
 * 订单：前台提交与历史查询；扩展方法配合 {@link com.lin101.store.vo.OrderSubmitReq}、{@link com.lin101.store.vo.OrderVO}。
 */
public interface OrderService extends IService<Order> {

    String submitOrder(OrderSubmitReq req);

    List<OrderVO> getUserOrders(Integer userId);
}