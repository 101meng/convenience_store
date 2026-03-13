package com.lin101.convenience_store.data.model

data class Product(
    val productId: Int,
    val categoryId: Int,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val tag1: String? = null,
    val tag2: String? = null,
    val tag3: String? = null
)