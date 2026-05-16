package com.lin101.store.service;

import java.util.List;
import java.util.Map;

public interface AiService {
    Map<String, Object> generateSmartCombo(String prompt, Integer storeId);
    Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems);
    String adminChat(String prompt) throws Exception;
}