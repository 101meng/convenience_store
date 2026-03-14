package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.CartService;
import com.lin101.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车请求接收器
 * 负责接收 Android 客户端发来的 HTTP 请求，并调用 Service 处理，最后打包成 JSON 返回
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestBody Map<String, Integer> requestData) {
        try {
            Integer userId = requestData.get("userId");
            Integer productId = requestData.get("productId");
            Integer quantity = requestData.get("quantity");

            cartService.addToCart(userId, productId, quantity);

            // 纯枚举驱动：加入购物车成功
            return Result.success(ResultCode.CART_ADD_SUCCESS);
        } catch (Exception e) {
            // 纯枚举驱动：加入购物车失败
            return Result.failed(ResultCode.CART_ADD_FAILED);
        }
    }

    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(@RequestParam("userId") Integer userId) {
        try {
            List<CartVO> cartList = cartService.getUserCartList(userId);
            // 获取列表成功，复用通用 SUCCESS 状态码
            return Result.success(ResultCode.SUCCESS, cartList);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_LIST_FAILED);
        }
    }

    @PutMapping("/update")
    public Result<Void> updateQuantity(@RequestBody Map<String, Integer> requestData) {
        try {
            Integer cartId = requestData.get("cartId");
            Integer quantity = requestData.get("quantity");

            cartService.updateCartQuantity(cartId, quantity);

            return Result.success(ResultCode.CART_UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_UPDATE_FAILED);
        }
    }

    @DeleteMapping("/remove")
    public Result<Void> removeCartItem(@RequestParam("cartId") Integer cartId) {
        try {
            cartService.removeCartItem(cartId);
            return Result.success(ResultCode.CART_REMOVE_SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_REMOVE_FAILED);
        }
    }
}