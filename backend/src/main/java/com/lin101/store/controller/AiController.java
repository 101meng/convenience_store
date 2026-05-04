package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 智能调度核心控制器
 * 毕设亮点：接收用户的自然语言描述，返回场景化商品推荐组合
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/planner")
    public Result<Map<String, Object>> getAiRecommendation(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return Result.failed(ResultCode.VALIDATE_FAILED);
            }

            // 调用 AI 服务层，获取包含文字回复和商品列表的混合结果
            Map<String, Object> aiResult = aiService.generateSmartCombo(prompt);

            return Result.success(ResultCode.SUCCESS, aiResult);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }
    @PostMapping("/dietitian")
    public Result<Map<String, Object>> analyzeDiet(@RequestBody Map<String, Object> request) {
        try {
            // 获取购物车商品列表
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) request.get("cartItems");
            Map<String, Object> result = aiService.analyzeNutrition(cartItems);
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }
}