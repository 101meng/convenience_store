package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 大模型相关接口：调用 LongCat Chat API（密钥与 URL 配置在 {@link com.lin101.store.service.impl.AiServiceImpl}）。
 * <p>首页场景推荐与购物车营养分析均在此暴露为 REST。</p>
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 场景化搭配：根据用户自然语言从全量商品中选 ID，并返回文案与商品列表。
     *
     * @param request JSON：{@code prompt} 必填
     */
    @PostMapping("/planner")
    public Result<Map<String, Object>> getAiRecommendation(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return Result.failed(ResultCode.VALIDATE_FAILED);
            }

            Map<String, Object> aiResult = aiService.generateSmartCombo(prompt);

            return Result.success(ResultCode.SUCCESS, aiResult);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    /**
     * 购物车营养分析：请求体携带 {@code cartItems}（含 {@code productId}、{@code quantity}），返回评分与建议列表。
     */
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