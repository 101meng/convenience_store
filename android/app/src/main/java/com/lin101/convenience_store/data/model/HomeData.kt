package com.lin101.convenience_store.data.model

data class Banner(
    val id: Int,
    val imageUrl: String,
    val linkUrl: String?
)

data class HomeData(
    val banners: List<Banner>,
    val flashSales: List<Product>,
    val newArrivals: List<Product>
)