package com.lin101.store.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 给前端返回的聚合商品信息 (包含 SPU 基础信息 + 门店专属 SKU 信息)
 */
@Data
public class ProductVO {
    private Integer productId;
    private Integer categoryId;
    private String name;
    private String description;

    // --- 以下 3 个字段来自 store_products 表（千店千面核心） ---
    private BigDecimal price; // 该门店的专属售价
    private Integer stock;    // 该门店的独立库存
    private Integer status;   // 该门店的上架状态
    // -------------------------------------------------------------

    private BigDecimal originalPrice;
    private Integer isFlashSale;
    private LocalDateTime flashSaleEndTime;
    private String imageUrl;

    private String tag1;
    private String tag2;
    private String tag3;

    // 必须完整返回，否则安卓端的 AI 营养师无法正常分析
    private Integer calories;
    private Integer protein;
    private Integer totalFat;
}