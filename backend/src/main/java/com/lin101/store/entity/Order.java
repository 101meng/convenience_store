package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单总表实体类
 * 对应数据库的 `orders` 表
 */
@Data
@TableName("orders")
public class Order {

    // 订单表自增主键
    @TableId(type = IdType.AUTO)
    private Integer orderId;

    // 唯一的订单流水号 (例如：ORD-2026-XXXX)
    private String orderSn;

    // 关联的用户 ID
    private Integer userId;

    // 关联的门店 ID
    private Integer storeId;

    // 商品总金额 (不含运费)
    private Double totalAmount;

    // 运费或自提费
    private Double deliveryFee;

    // 用户实际需要支付的最终金额
    private Double actualAmount;

    // 订单类型：'shipping' 或 'pickup'
    private String orderType;

    // 支付方式
    private String paymentMethod;

    // 订单状态：'pending'(待处理), 'delivering'(配送中), 'completed'(已完成), 'cancelled'(已取消)
    private String status;

    // 收货地址
    private String deliveryAddress;

    private LocalDateTime createdAt;

}