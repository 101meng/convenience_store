package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Order;
import com.lin101.store.vo.OrderSubmitReq;
import com.lin101.store.vo.OrderVO;

import java.util.List;

/**
 * 订单业务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 核心业务：提交订单
     * @param req 前端传来的下单参数
     * @return 成功后生成的订单流水号 (OrderSn)
     */
    String submitOrder(OrderSubmitReq req);
    // 获取用户的所有历史订单
    List<OrderVO> getUserOrders(Integer userId);
}