package com.lin101.store.vo;

import lombok.Data;

@Data
public class OrderItemVO {
    private Integer productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double priceAtTime;
}