package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 表 order_items：行级明细；单价为下单快照。 */
@Data
@TableName("order_items")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Integer itemId;

    private Integer orderId;

    private Integer productId;

    private Integer quantity;

    /** 下单时单价快照 */
    private Double priceAtTime;
}