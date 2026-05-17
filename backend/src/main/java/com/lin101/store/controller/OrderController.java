package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.interceptor.JwtInterceptor;
import com.lin101.store.service.OrderService;
import com.lin101.store.vo.OrderSubmitReq;
import com.lin101.store.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单接口（需 JWT）。提交订单会基于服务端购物车重算金额并事务写库；列表接口按用户返回订单及明细。
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单：购物车为空时返回 {@link com.lin101.store.common.ResultCode#ORDER_CART_EMPTY}。
     *
     * @param req 结算页提交的订单类型、运费、地址等；用户身份与门店上下文来自请求
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId,
            @RequestBody OrderSubmitReq req) {
        try {
            String orderSn = orderService.submitOrder(userId, storeId, req);

            return Result.success(ResultCode.ORDER_SUBMIT_SUCCESS, orderSn);

        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (IllegalStateException e) {
            return Result.failed(ResultCode.ORDER_CART_EMPTY);
        } catch (SecurityException e) {
            return Result.failed(ResultCode.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.ORDER_SUBMIT_FAILED);
        }
    }

    @PostMapping("/pay")
    public Result<Void> payOrder(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestParam("orderId") Integer orderId) {
        try {
            orderService.payOrder(userId, orderId);
            return Result.success(ResultCode.ORDER_PAY_SUCCESS);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (IllegalStateException e) {
            return Result.failed(ResultCode.ORDER_PAY_FAILED);
        } catch (SecurityException e) {
            return Result.failed(ResultCode.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.ORDER_PAY_FAILED);
        }
    }

    /**
     * 历史订单列表（含每条订单的商品明细与展示用时间格式）。
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId) {
        try {
            return Result.success(ResultCode.SUCCESS, orderService.getUserOrders(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/receive")
    public Result<Void> receiveOrder(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestParam("orderId") Integer orderId) {
        try {
            orderService.receiveOrder(userId, orderId);
            return Result.success(ResultCode.ORDER_RECEIVE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (IllegalStateException e) {
            return Result.failed(ResultCode.ORDER_RECEIVE_FAILED);
        } catch (SecurityException e) {
            return Result.failed(ResultCode.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.ORDER_RECEIVE_FAILED);
        }
    }
}
