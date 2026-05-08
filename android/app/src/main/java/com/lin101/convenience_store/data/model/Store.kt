package com.lin101.convenience_store.data.model

data class Store(
    val storeId: Int,
    val storeName: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?
)