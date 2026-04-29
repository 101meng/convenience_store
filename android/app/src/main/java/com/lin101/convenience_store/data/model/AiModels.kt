package com.lin101.convenience_store.data.model

class AiModels {
    // 发送给后端的用户的自然语言需求
    data class AiPlannerReq(
        val prompt: String
    )

    // 后端 AI 返回的推荐结果
    data class AiPlannerResp(
        val aiMessage: String,          // AI 的幽默/贴心回复文字
        val recommendedProducts: List<Product> // AI 从数据库挑出来的商品实体
    )
}