package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.Cart;
import com.lin101.store.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车 Mapper 接口
 * 继承 BaseMapper 后，自动获得单表的增删改查能力
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 自定义 SQL 方法：多表联查获取用户的购物车完整信息
     * @param userId 需要查询的用户 ID
     * @return 拼装了商品详情的购物车列表
     */
    @Select("SELECT c.cart_id, c.user_id, c.product_id, c.quantity, p.name, p.price, p.image_url " +
            "FROM cart c LEFT JOIN products p ON c.product_id = p.product_id " +
            "WHERE c.user_id = #{userId}")
    List<CartVO> getCartItemsWithProductInfo(Integer userId);
}