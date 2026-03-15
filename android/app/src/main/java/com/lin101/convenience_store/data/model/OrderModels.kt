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

    data class OrderItemVO(
        val productId: Int,
        val name: String,
        val imageUrl: String?,
        val quantity: Int,
        val priceAtTime: Double
    )

    // 找到 OrderVO，修改成这样：
    data class OrderVO(
        val orderId: Int,
        val orderSn: String?,
        val actualAmount: Double,
        val status: String?,
        val orderType: String?,
        val createdAt: String?,
        val items: List<OrderItemVO>
    )
}