package com.lin101.store.vo;

import lombok.Data;

/** 订单行展示：商品信息与快照价。 */
@Data
public class OrderItemVO {
    private Integer productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double priceAtTime;
}