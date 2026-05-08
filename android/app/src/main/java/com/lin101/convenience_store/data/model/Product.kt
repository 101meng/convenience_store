package com.lin101.convenience_store.data.model

/**
 * 修改后的商品数据模型
 * 增加了与数据库 SQL 对应的营养成分字段
 */
data class Product(
    val productId: Int,
    val categoryId: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val originalPrice: Double?,
    val isFlashSale: Int?,
    val flashSaleEndTime: String?,
    val imageUrl: String?,
    val tag1: String? = null,
    val tag2: String? = null,
    val tag3: String? = null,
    val calories: Int? = 0,
    val protein: Int? = 0,
    val totalFat: Int? = 0
)