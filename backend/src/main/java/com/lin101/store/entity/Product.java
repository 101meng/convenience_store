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

    // original_price 建议保留作为商品的“划线价”或“指导价”
    private BigDecimal originalPrice;

    private Integer isFlashSale;
    private LocalDateTime flashSaleEndTime;
    private String imageUrl;

    // 营销标签
    private String tag1;
    private String tag2;
    private String tag3;

    // AI 营养成分分析字段
    private Integer calories;
    private Integer protein;
    private Integer totalFat;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}