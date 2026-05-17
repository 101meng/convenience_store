package com.lin101.store.vo;

import lombok.Data;

/** 下单请求体（结算页提交）。 */
@Data
public class OrderSubmitReq {
    private String orderType;

    private String paymentMethod;

    private String deliveryAddress;

    private Double deliveryFee;
}
