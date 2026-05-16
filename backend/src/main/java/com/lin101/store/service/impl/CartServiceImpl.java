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

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void addToCart(Integer userId, Integer productId, Integer quantity, Integer storeId) {
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
        return cartMapper.getCartItemsWithProductInfo(userId, storeId);
    }

    @Override
    public void updateCartQuantity(Integer cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart != null) {
            if (quantity <= 0) {
                this.removeById(cartId);
            } else {
                cart.setQuantity(quantity);
                this.updateById(cart);
            }
        }
    }

    @Override
    public void removeCartItem(Integer cartId) {
        this.removeById(cartId);
    }

}