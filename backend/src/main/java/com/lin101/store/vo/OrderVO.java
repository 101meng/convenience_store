package com.lin101.store.vo;

import lombok.Data;
import java.util.List;

@Data
public class OrderVO {
    private Integer orderId;
    private String orderSn;
    private Double actualAmount;
    private String status;
    private String orderType;
    private String createdAt;
    // 包含的商品明细列表
    private List<OrderItemVO> items;
}