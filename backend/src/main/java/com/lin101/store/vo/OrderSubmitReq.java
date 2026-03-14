package com.lin101.store.vo;

import lombok.Data;

/**
 * 订单提交请求体对象 (Data Transfer Object)
 * 专门用于接收 Android 端 CheckoutScreen 发送过来的 JSON 数据
 */
@Data
public class OrderSubmitReq {
    // 下单用户的 ID
    private Integer userId;

    // 门店 ID (如果是自提，会传 1 或 2；如果是配送，可以为空)
    private Integer storeId;

    // 订单类型：'shipping' (配送) 或 'pickup' (自提)
    private String orderType;

    // 支付方式：'WeChat Pay', 'Alipay', 'Apple Pay' 等
    private String paymentMethod;

    // 配送地址 (如果是自提，可以为空)
    private String deliveryAddress;

    // 运费/自提费 (由前端计算好传过来，后端也可以复核)
    private Double deliveryFee;
}