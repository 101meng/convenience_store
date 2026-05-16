package com.lin101.store.service;

import java.util.List;
import java.util.Map;

/** 大模型门面：场景推荐、购物车营养、管理端对话（实现见 {@link com.lin101.store.service.impl.AiServiceImpl}）。 */
public interface AiService {
    Map<String, Object> generateSmartCombo(String prompt);

    Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems);

    String adminChat(String prompt) throws Exception;
}