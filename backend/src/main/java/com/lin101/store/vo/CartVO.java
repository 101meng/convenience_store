package com.lin101.store.vo;

import lombok.Data;

/**
 * 购物车视图对象 (View Object)
 * 专门用来把数据库多张表的数据拼装在一起，返回给前端展示
 * 因为前端除了需要购物车的数量，还需要商品的图片、名字、价格等详情
 */
@Data
public class CartVO {
    // 来自 cart 表的字段
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    // 来自 products 表的补充字段
    private String name;        // 商品名称
    private Double price;       // 商品价格
    private String imageUrl;    // 商品图片路径
}