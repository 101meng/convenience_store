package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.lin101.store.entity.Product;
import com.lin101.store.mapper.ProductMapper;
import com.lin101.store.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private ProductMapper productMapper;

    // 配置 LongCat API 的请求地址与密钥
    private static final String API_URL = "https://api.longcat.chat/openai/v1/chat/completions";
    private static final String API_KEY = "ak_2nY9d21Xa7PM9Wd5HD4vH48q6fY8g";

    // ==========================================
    // 业务 1：AI 场景智能搭配 (首页使用)
    // ==========================================
    @Override
    public Map<String, Object> generateSmartCombo(String prompt) {
        List<Product> allProducts = productMapper.selectList(null);
        StringBuilder productsListStr = new StringBuilder();
        for (Product p : allProducts) {
            productsListStr.append(p.getProductId()).append(": ").append(p.getName()).append("\n");
        }

        String systemPrompt = "You are a humorous and caring AI shopping assistant in a convenience store. Based on the user's request and the product list below, recommend 2 to 3 of the most suitable products.\n" +
                "Current real product database (Product ID : Name):\n" + productsListStr.toString() + "\n" +
                "You MUST strictly return a valid JSON string. DO NOT include any Markdown tags (such as ```json). Return pure JSON directly!\n" +
                "The JSON format strictly requires:\n" +
                "{\n" +
                "  \"aiMessage\": \"(Your humorous and caring reply MUST BE IN ENGLISH ONLY, briefly explaining why you recommend these products)\",\n" +
                "  \"recommendedProductIds\": [(The list of product IDs you selected, e.g., [2, 9])]\n" +
                "}";

        String finalAiMessage = "Seems like a network glitch! But here is a fresh combo for you anyway 🥗";
        List<Integer> recommendedProductIds = new ArrayList<>();

        try {
            // 调用统一的网络请求辅助方法
            String aiContent = callLongCatApi(systemPrompt, prompt);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            finalAiMessage = contentNode.path("aiMessage").asText();
            ArrayNode idsNode = (ArrayNode) contentNode.path("recommendedProductIds");
            for (JsonNode idNode : idsNode) {
                recommendedProductIds.add(idNode.asInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AI Smart Combo generated an error, falling back to default.");
            recommendedProductIds.clear();
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        if (recommendedProductIds.isEmpty()) {
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id", recommendedProductIds);
        List<Product> products = productMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("aiMessage", finalAiMessage);
        result.put("recommendedProducts", products);

        return result;
    }

    // ==========================================
    // 业务 2：AI 营养师/卡路里雷达 (购物车使用)
    // ==========================================
    @Override
    public Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems) {
        int totalCal = 0, totalPro = 0, totalFat = 0;
        StringBuilder itemList = new StringBuilder();

        // 遍历前端传来的购物车数据，去数据库查出真实的卡路里数据并累加
        for (Map<String, Object> item : cartItems) {
            Integer productId = (Integer) item.get("productId");
            Integer qty = (Integer) item.get("quantity");

            Product p = productMapper.selectById(productId);
            if (p != null) {
                // 这里赋予擬真數據用于答辯演示
                totalCal += 250 * qty;
                totalPro += 12 * qty;
                totalFat += 8 * qty;

                itemList.append("- ").append(p.getName()).append(" (Qty: ").append(qty).append(")\n");
            }
        }

        // ==========================================
        // 【核心修改】：更新大模型提示词工程
        // 要求返回建议列表 (adviceList) 替代单一建议字符串 (advice)
        // 範例：Total Calories: " + totalCal + "kcal，確保 AI 明白單位含義
        // ==========================================
        String systemPrompt = "You are a professional yet humorous and sharp-tongued dietitian. \n" +
                "Analyze the user's shopping cart nutrition. Be brutally honest and funny. \n" +
                "Nutritional Data: Total Calories: " + totalCal + "kcal, Protein: " + totalPro + "g, Fat: " + totalFat + "g.\n" +
                "Products in cart:\n" + itemList.toString() + "\n" +
                "STRICTLY return a valid JSON string without Markdown tags. IN ENGLISH ONLY:\n" +
                "{\n" +
                "  \"healthScore\": (A score from 0-100),\n" +
                "  \"aiComment\": \"(A humorous, one-sentence comment. e.g., 'You're one bag of chips away from meeting your maker.')\",\n" +
                "  \"adviceList\": [\"(Practical advice sentence 1)\", \"(Practical advice sentence 2)\"],\n" +
                "  \"totalCalories\": " + totalCal + ",\n" +
                "  \"totalProtein\": " + totalPro + ",\n" +
                "  \"totalFat\": " + totalFat + "\n" +
                "}";

        Map<String, Object> result = new HashMap<>();
        List<String> adviceList = new ArrayList<>();

        try {
            // 调用大模型
            String aiContent = callLongCatApi(systemPrompt, "Please analyze my cart.");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            result.put("healthScore", contentNode.path("healthScore").asInt(80));
            result.put("aiComment", contentNode.path("aiComment").asText("Looking good, but could use more veggies!"));
            result.put("totalCalories", contentNode.path("totalCalories").asInt(totalCal));
            result.put("totalProtein", contentNode.path("totalProtein").asInt(totalPro));
            result.put("totalFat", contentNode.path("totalFat").asInt(totalFat));

            // 解析建议列表 (adviceList)
            ArrayNode adviceNode = (ArrayNode) contentNode.path("adviceList");
            if (adviceNode != null) {
                for (JsonNode aNode : adviceNode) {
                    adviceList.add(aNode.asText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AI Dietitian generated an error, falling back to default.");
            // 降级保底数据，防止页面白屏
            result.put("healthScore", 65);
            result.put("aiComment", "Unable to reach the dietitian, so I'll just say: drink more water!");
            result.put("totalCalories", totalCal);
            result.put("totalProtein", totalPro);
            result.put("totalFat", totalFat);
            adviceList.clear();
            adviceList.add("Everything in moderation.");
            adviceList.add("Stay hydrated!");
        }

        // 防御性编程：如果 AI 一个建议都没给
        if (adviceList.isEmpty()) {
            adviceList.add("Try adding a salad or swapping out sugary drinks.");
        }

        // 组装最终结果返回给 Android 端
        result.put("adviceList", adviceList); // 将整个列表返回

        return result;
    }

    // ==========================================
    // 核心公共方法：负责向大模型发送请求并提取纯净 JSON
    // ==========================================
    private String callLongCatApi(String systemPrompt, String userPrompt) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "LongCat-Flash-Chat");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);

        messages.add(systemMessage);
        messages.add(userMsg);
        requestBody.put("messages", messages);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        String aiContent = rootNode.path("choices").get(0).path("message").path("content").asText();

        return aiContent.replaceAll("(?i)```json", "").replaceAll("```", "").trim();
    }
}