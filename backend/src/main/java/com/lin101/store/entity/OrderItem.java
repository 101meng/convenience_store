package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单商品明细表实体类
 * 对应数据库的 `order_items` 表
 */
@Data
@TableName("order_items")
public class OrderItem {

    // 明细表自增主键
    @TableId(type = IdType.AUTO)
    private Integer itemId;

    // 关联的总订单 ID
    private Integer orderId;

    // 关联的商品 ID
    private Integer productId;

    // 购买的商品数量
    private Integer quantity;

    // 【关键设计】：下单时的商品单价 (防止未来商品涨价导致历史订单金额变化)
    private Double priceAtTime;
}