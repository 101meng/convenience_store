package com.lin101.convenience_store.data.model

class AiModels {
    // AI 场景搭配（保留之前的）
    data class AiPlannerReq(val prompt: String)
    data class AiPlannerResp(val aiMessage: String, val recommendedProducts: List<Product>)

    // AI 营养师请求体
    data class AiDietitianReq(
        val cartItems: List<CartItem>
    )
    data class AiDietitianResp(
        val healthScore: Int,      // 健康评分 0-100
        val aiComment: String,     // 幽默点评
        val adviceList: List<String>, // Practical advice items (list)
        val totalCalories: Int,
        val totalProtein: Int,
        val totalFat: Int
    )
}