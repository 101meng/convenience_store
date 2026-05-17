package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Cart;
import com.lin101.store.mapper.CartMapper;
import com.lin101.store.service.CartService;
import com.lin101.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void addToCart(Integer userId, Integer productId, Integer quantity, Integer storeId) {
        if (userId == null || productId == null || quantity == null || storeId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid cart add request");
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("product_id", productId)
                .eq("store_id", storeId);

        Cart existingCart = this.getOne(queryWrapper);

        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + quantity;
            existingCart.setQuantity(newQuantity);
            this.updateById(existingCart);
        } else {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setStoreId(storeId);
            newCart.setProductId(productId);
            newCart.setQuantity(quantity);
            this.save(newCart);
        }
    }

    @Override
    public List<CartVO> getUserCartList(Integer userId, Integer storeId) {
        if (userId == null || storeId == null) {
            throw new IllegalArgumentException("Invalid cart list request");
        }
        return cartMapper.getCartItemsWithProductInfo(userId, storeId);
    }

    @Override
    public void updateCartQuantity(Integer userId, Integer cartId, Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        }

        Cart cart = getOwnedCart(userId, cartId);
        if (quantity <= 0) {
            this.removeById(cartId);
        } else {
            cart.setQuantity(quantity);
            this.updateById(cart);
        }
    }

    @Override
    public void removeCartItem(Integer userId, Integer cartId) {
        getOwnedCart(userId, cartId);
        this.removeById(cartId);
    }

    private Cart getOwnedCart(Integer userId, Integer cartId) {
        if (userId == null || cartId == null) {
            throw new IllegalArgumentException("Cart id is required");
        }

        Cart cart = this.getById(cartId);
        if (cart == null) {
            throw new IllegalStateException("Cart item not found");
        }
        if (!Objects.equals(cart.getUserId(), userId)) {
            throw new SecurityException("Cart item does not belong to current user");
        }
        return cart;
    }
}
