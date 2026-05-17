package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 场景化搭配：根据用户自然语言从当前门店商品中选 ID，并返回文案与商品列表。
     * 门店 ID 从请求头 X-Store-Id 获取（默认 1）
     */
    @PostMapping("/planner")
    public Result<Map<String, Object>> getAiRecommendation(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return Result.failed(ResultCode.VALIDATE_FAILED);
            }

            Map<String, Object> aiResult = aiService.generateSmartCombo(prompt, storeId);

            return Result.success(ResultCode.SUCCESS, aiResult);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/dietitian")
    public Result<Map<String, Object>> analyzeDiet(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) request.get("cartItems");
            Map<String, Object> result = aiService.analyzeNutrition(cartItems);
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }
}