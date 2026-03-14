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

/**
 * 购物车业务具体实现类
 * 所有的“思考逻辑”都在这里转化为实际的代码
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    // 注入我们刚才写的 Mapper，用来执行多表联查 SQL
    @Autowired
    private CartMapper cartMapper;

    /**
     * 实现：将商品加入购物车
     */
    @Override
    public void addToCart(Integer userId, Integer productId, Integer quantity) {
        // 第一步：构建查询条件，去数据库找找这个人是不是已经加过这个商品了
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("product_id", productId);

        // 执行单条查询
        Cart existingCart = this.getOne(queryWrapper);

        if (existingCart != null) {
            // 如果查到了，说明购物车里有，那就把原来的数量加上新加的数量
            int newQuantity = existingCart.getQuantity() + quantity;
            existingCart.setQuantity(newQuantity);
            // 更新回数据库
            this.updateById(existingCart);
        } else {
            // 如果没查到，说明是全新的商品，创建一个新的 Cart 实体，准备插入数据库
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(productId);
            newCart.setQuantity(quantity);
            // 保存到数据库
            this.save(newCart);
        }
    }

    /**
     * 实现：获取用户的购物车完整列表
     */
    @Override
    public List<CartVO> getUserCartList(Integer userId) {
        // 直接调用 Mapper 里我们自己写的 SQL
        return cartMapper.getCartItemsWithProductInfo(userId);
    }

    /**
     * 实现：修改购物车内商品的数量
     */
    @Override
    public void updateCartQuantity(Integer cartId, Integer quantity) {
        // 如果前端传过来的数量小于等于 0，我们直接认为用户是不想要了，执行删除逻辑
        if (quantity <= 0) {
            this.removeById(cartId);
            return;
        }

        // 构建一个新的 Cart 对象，只放入主键和要修改的字段
        Cart updateCart = new Cart();
        updateCart.setCartId(cartId);
        updateCart.setQuantity(quantity);
        // MyBatisPlus 会根据主键自动更新传入的非空字段
        this.updateById(updateCart);
    }

    /**
     * 实现：移除某条购物车记录
     */
    @Override
    public void removeCartItem(Integer cartId) {
        // MyBatisPlus 自带的主键删除方法
        this.removeById(cartId);
    }
}