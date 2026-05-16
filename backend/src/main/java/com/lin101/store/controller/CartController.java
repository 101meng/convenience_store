package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.CartService;
import com.lin101.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(
            @RequestBody Map<String, Integer> requestData,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            Integer userId = requestData.get("userId");
            Integer productId = requestData.get("productId");
            Integer quantity = requestData.get("quantity");

            cartService.addToCart(userId, productId, quantity, storeId);

            return Result.success(ResultCode.CART_ADD_SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_ADD_FAILED);
        }
    }

    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(
            @RequestParam("userId") Integer userId,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            List<CartVO> cartList = cartService.getUserCartList(userId, storeId);
            return Result.success(ResultCode.SUCCESS, cartList);
        } catch (Exception e) {
            e.printStackTrace();  // 打印完整堆栈
            return Result.failed(ResultCode.CART_LIST_FAILED);
        }
    }

    @PutMapping("/update")
    public Result<Void> updateCartItem(@RequestBody Map<String, Integer> requestData) {
        try {
            Integer cartId = requestData.get("cartId");
            Integer quantity = requestData.get("quantity");
            cartService.updateCartQuantity(cartId, quantity);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_UPDATE_FAILED);
        }
    }

    @DeleteMapping("/delete/{cartId}")
    public Result<Void> removeCartItem(@PathVariable Integer cartId) {
        try {
            cartService.removeCartItem(cartId);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_DELETE_FAILED);
        }
    }
}