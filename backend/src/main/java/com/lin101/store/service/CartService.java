package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Cart;
import com.lin101.store.vo.CartVO;

import java.util.List;

/**
 * 购物车业务层接口
 * 定义购物车相关的所有业务动作规范
 */
public interface CartService extends IService<Cart> {

    // 动作 1：将商品加入购物车（包含判断是新增还是更新数量的逻辑）
    void addToCart(Integer userId, Integer productId, Integer quantity);

    // 动作 2：获取用户当前购物车的详细列表（带商品信息）
    List<CartVO> getUserCartList(Integer userId);

    // 动作 3：修改购物车中某个商品的数量（比如点击了 + 号或 - 号）
    void updateCartQuantity(Integer cartId, Integer quantity);

    // 动作 4：将某条记录移出购物车（比如滑动删除）
    void removeCartItem(Integer cartId);
}