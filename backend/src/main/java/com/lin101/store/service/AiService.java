package com.lin101.store.service;

import java.util.List;
import java.util.Map;

public interface AiService {
    /**
     * 场景化智能搭配推荐
     */
    Map<String, Object> generateSmartCombo(String prompt);

    /**
     * 购物车营养师分析
     */
    Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems);

    // 后台 AI 助理对话
    String adminChat(String prompt) throws Exception;
}