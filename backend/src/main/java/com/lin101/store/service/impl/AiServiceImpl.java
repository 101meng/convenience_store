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

    @Override
    public Map<String, Object> generateSmartCombo(String prompt) {

        // 1. 获取数据库中所有的真实商品
        List<Product> allProducts = productMapper.selectList(null);
        StringBuilder productsListStr = new StringBuilder();
        for (Product p : allProducts) {
            productsListStr.append(p.getProductId()).append(": ").append(p.getName()).append("\n");
        }

        // ==========================================
        // 【核心修改】：将系统提示词全部改为英文，并强制要求大模型输出纯英文
        // ==========================================
        String systemPrompt = "You are a humorous and caring AI shopping assistant in a convenience store. Based on the user's request and the product list below, recommend 2 to 3 of the most suitable products.\n" +
                "Current real product database (Product ID : Name):\n" + productsListStr.toString() + "\n" +
                "You MUST strictly return a valid JSON string. DO NOT include any Markdown tags (such as ```json). Return pure JSON directly!\n" +
                "The JSON format strictly requires:\n" +
                "{\n" +
                "  \"aiMessage\": \"(Your humorous and caring reply MUST BE IN ENGLISH ONLY, briefly explaining why you recommend these products)\",\n" +
                "  \"recommendedProductIds\": [(The list of product IDs you selected, e.g., [2, 9])]\n" +
                "}";

        // 3. 构造请求体 (标准 OpenAI 格式)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "LongCat-Flash-Chat");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        messages.add(systemMessage);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        // 设置请求头
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 英文版的失败保底文案
        String finalAiMessage = "Seems like a network glitch! But here is a fresh combo for you anyway 🥗";
        List<Integer> recommendedProductIds = new ArrayList<>();

        try {
            // 4. 发起真实的网络请求给 LongCat 大模型
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            // 5. 使用 Jackson 解析大模型返回的数据
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            String aiContent = rootNode.path("choices").get(0).path("message").path("content").asText();

            // 清理大模型有时喜欢强行加上的 Markdown 格式符
            aiContent = aiContent.replaceAll("(?i)```json", "").replaceAll("```", "").trim();

            JsonNode contentNode = mapper.readTree(aiContent);
            finalAiMessage = contentNode.path("aiMessage").asText();

            // 提取推荐的商品 ID 数组
            ArrayNode idsNode = (ArrayNode) contentNode.path("recommendedProductIds");
            for (JsonNode idNode : idsNode) {
                recommendedProductIds.add(idNode.asInt());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("大模型调用或解析失败，启动降级方案...");
            recommendedProductIds.clear();
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        if (recommendedProductIds.isEmpty()) {
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        // 6. 拿着最终决定好的商品 ID，去数据库查询商品详细信息
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id", recommendedProductIds);
        List<Product> products = productMapper.selectList(queryWrapper);

        if (products.isEmpty()) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.in("product_id", 1, 11);
            products = productMapper.selectList(queryWrapper);
        }

        // 7. 组装最终结果返回给 Android 客户端
        Map<String, Object> result = new HashMap<>();
        result.put("aiMessage", finalAiMessage);
        result.put("recommendedProducts", products);

        return result;
    }
}