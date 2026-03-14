package com.lin101.convenience_store.data.model

class OrderModels {
    /**
     * 对应后端的 OrderSubmitReq (下单请求体)
     */
    data class OrderSubmitReq(
        val userId: Int,
        val storeId: Int?,
        val orderType: String,
        val paymentMethod: String,
        val deliveryAddress: String?,
        val deliveryFee: Double
    )
}