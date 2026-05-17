package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.interceptor.JwtInterceptor;
import com.lin101.store.service.CartService;
import com.lin101.store.vo.CartAddReq;
import com.lin101.store.vo.CartUpdateReq;
import com.lin101.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestBody CartAddReq requestData,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            cartService.addToCart(userId, requestData.getProductId(), requestData.getQuantity(), storeId);
            return Result.success(ResultCode.CART_ADD_SUCCESS);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_ADD_FAILED);
        }
    }

    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            List<CartVO> cartList = cartService.getUserCartList(userId, storeId);
            return Result.success(ResultCode.SUCCESS, cartList);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_LIST_FAILED);
        }
    }

    @PutMapping("/update")
    public Result<Void> updateCartItem(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestBody CartUpdateReq requestData) {
        try {
            cartService.updateCartQuantity(userId, requestData.getCartId(), requestData.getQuantity());
            return Result.success(ResultCode.CART_UPDATE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (SecurityException e) {
            return Result.failed(ResultCode.FORBIDDEN);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_UPDATE_FAILED);
        }
    }

    @DeleteMapping("/delete/{cartId}")
    public Result<Void> removeCartItem(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @PathVariable Integer cartId) {
        try {
            cartService.removeCartItem(userId, cartId);
            return Result.success(ResultCode.CART_REMOVE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);
        } catch (SecurityException e) {
            return Result.failed(ResultCode.FORBIDDEN);
        } catch (Exception e) {
            return Result.failed(ResultCode.CART_REMOVE_FAILED);
        }
    }
}
