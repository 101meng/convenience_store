package com.lin101.convenience_store.data.model

data class Product(
    val productId: Int,
    val categoryId: Int,
    val name: String,
    val description: String?,
    val price: Double,           // 门店专属价
    val stock: Int = 0,          // 门店独立库存
    val status: Int? = 1,        // 门店上架状态

    val originalPrice: Double?,
    val isFlashSale: Int?,
    val flashSaleEndTime: String?,
    val imageUrl: String?,

    // 营销标签
    val tag1: String? = null,
    val tag2: String? = null,
    val tag3: String? = null,

    // AI 营养成分分析字段
    val calories: Int? = 0,
    val protein: Int? = 0,
    val totalFat: Int? = 0
)