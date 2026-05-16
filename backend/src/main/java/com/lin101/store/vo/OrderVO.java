package com.lin101.store.vo;

import lombok.Data;
import java.util.List;

/** 订单列表/详情展示（含格式化时间与明细）。 */
@Data
public class OrderVO {
    private Integer orderId;
    private String orderSn;
    private Double actualAmount;
    private String status;
    private String orderType;
    private String createdAt;
    private String deliveryAddress;
    private List<OrderItemVO> items;
}