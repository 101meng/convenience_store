package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("store_products")
public class StoreProduct {
    private Integer id;
    private Integer storeId;
    private Integer productId;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
