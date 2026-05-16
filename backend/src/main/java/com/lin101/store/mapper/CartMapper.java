package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.Cart;
import com.lin101.store.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT c.cart_id, c.user_id, c.product_id, c.quantity, p.name, COALESCE(sp.store_price, 0) as price, p.image_url " +
            "FROM cart c " +
            "LEFT JOIN products p ON c.product_id = p.product_id " +
            "LEFT JOIN store_products sp ON c.product_id = sp.product_id AND sp.store_id = c.store_id " +
            "WHERE c.user_id = #{userId} AND c.store_id = #{storeId}")
    List<CartVO> getCartItemsWithProductInfo(@Param("userId") Integer userId, @Param("storeId") Integer storeId);
}
