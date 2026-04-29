package com.lin101.store.service;

import java.util.Map;

public interface AiService {
    /**
     * 根据用户的需求描述，生成智能搭配方案
     */
    Map<String, Object> generateSmartCombo(String prompt);
}