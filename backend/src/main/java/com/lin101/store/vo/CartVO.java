package com.lin101.store.vo;

import lombok.Data;

/** 购物车行 + 商品展示字段（联表查询结果）。 */
@Data
public class CartVO {
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    private String name;
    private Double price;
    private String imageUrl;
}