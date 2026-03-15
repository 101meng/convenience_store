package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.OrderService;
import com.lin101.store.vo.OrderSubmitReq;
import com.lin101.store.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 处理下单、订单查询等业务
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单接口
     * POST /api/order/submit
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody OrderSubmitReq req) {
        try {
            // 调用 Service 执行复杂的下单事务
            String orderSn = orderService.submitOrder(req);

            // 纯枚举驱动：返回成功状态，并将订单流水号发给前端
            return Result.success(ResultCode.ORDER_SUBMIT_SUCCESS, orderSn);

        } catch (IllegalArgumentException e) {
            // 捕获购物车为空的特例异常
            return Result.failed(ResultCode.ORDER_CART_EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
            // 纯枚举驱动：其他未知失败
            return Result.failed(ResultCode.ORDER_SUBMIT_FAILED);
        }
    }

    /**
     * 获取历史订单列表
     * GET /api/order/list?userId=1
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(@RequestParam("userId") Integer userId) {
        try {
            return Result.success(ResultCode.SUCCESS, orderService.getUserOrders(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }
}