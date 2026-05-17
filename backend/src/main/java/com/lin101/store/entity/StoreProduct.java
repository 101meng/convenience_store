package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("store_products")
public class StoreProduct {
    @TableId
    private Integer id;
    private Integer storeId;
    private Integer productId;

    @TableField("store_price")
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
