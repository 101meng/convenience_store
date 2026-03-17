package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer productId;
    private Integer categoryId;
    private String name;
    private String description;
    private BigDecimal price; // 价格推荐使用 BigDecimal 防止精度丢失
    private String imageUrl;
    private String unit;
    private Integer calories;
    private Integer protein;
    private Integer totalFat;
    private String shelfLife;
    private String tag1;
    private String tag2;
    private String tag3;
    private Double originalPrice;
    private Integer isFlashSale;
    private LocalDateTime flashSaleEndTime;
}