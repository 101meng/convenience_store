package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 表 orders：订单主信息。 */
@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Integer orderId;

    /** 业务单号，如 ORD-2026-xxxx */
    private String orderSn;

    private Integer userId;

    private Integer storeId;

    /** 商品小计，不含运费 */
    private Double totalAmount;

    private Double deliveryFee;

    private Double actualAmount;

    /** shipping / pickup */
    private String orderType;

    private String paymentMethod;

    /** pending / delivering / completed / cancelled */
    private String status;

    private String deliveryAddress;

    private LocalDateTime createdAt;

}