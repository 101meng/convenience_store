package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Cart;
import com.lin101.store.vo.CartVO;

import java.util.List;

public interface CartService extends IService<Cart> {

    void addToCart(Integer userId, Integer productId, Integer quantity, Integer storeId);

    List<CartVO> getUserCartList(Integer userId, Integer storeId);

    void updateCartQuantity(Integer cartId, Integer quantity);

    void removeCartItem(Integer cartId);
}