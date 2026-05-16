package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.lin101.store.entity.Product;
import com.lin101.store.mapper.ProductMapper;
import com.lin101.store.service.AiService;
import com.lin101.store.vo.ProductVO;
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

    private static final String API_URL = "https://api.longcat.chat/openai/v1/chat/completions";
    private static final String API_KEY = "ak_2nY9d21Xa7PM9Wd5HD4vH48q6fY8g";

    @Override
    public Map<String, Object> generateSmartCombo(String prompt, Integer storeId) {
        // 1. 查询当前门店有库存且上架的商品（只取 ID 和名称）
        List<Product> storeProducts = productMapper.getProductsByStoreId(storeId);

        // 如果门店没有商品，返回空推荐
        if (storeProducts == null || storeProducts.isEmpty()) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("aiMessage", "Sorry, no products available in this store currently.");
            fallback.put("recommendedProducts", new ArrayList<>());
            return fallback;
        }

        // 2. 构造商品清单字符串供 AI 参考
        StringBuilder productsListStr = new StringBuilder();
        for (Product p : storeProducts) {
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
        List<ProductVO> recommendedProducts = new ArrayList<>();

        try {
            String aiContent = callLongCatApi(systemPrompt, prompt);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            finalAiMessage = contentNode.path("aiMessage").asText();
            ArrayNode idsNode = (ArrayNode) contentNode.path("recommendedProductIds");
            for (JsonNode idNode : idsNode) {
                int pid = idNode.asInt();
                // 确保推荐的 ID 在门店商品列表中
                boolean exists = storeProducts.stream().anyMatch(p -> p.getProductId().equals(pid));
                if (exists) {
                    recommendedProductIds.add(pid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AI Smart Combo error, using fallback.");
        }

        // 如果推荐列表为空（解析失败或 ID 不在门店商品中），从门店商品中随机取 2 个作为降级
        if (recommendedProductIds.isEmpty()) {
            int size = Math.min(2, storeProducts.size());
            for (int i = 0; i < size; i++) {
                recommendedProductIds.add(storeProducts.get(i).getProductId());
            }
            finalAiMessage = "Sorry, I couldn't connect to my brain. But here are some popular items in this store!";
        }

        // 3. 查询推荐商品的完整信息（含门店价格）
        if (!recommendedProductIds.isEmpty()) {
            recommendedProducts = productMapper.getStoreProductsByIds(storeId, recommendedProductIds);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("aiMessage", finalAiMessage);
        result.put("recommendedProducts", recommendedProducts);

        return result;
    }
    @Override
    public Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems) {
        int totalCal = 0, totalPro = 0, totalFat = 0;
        StringBuilder itemList = new StringBuilder();
        for (Map<String, Object> item : cartItems) {
            Integer productId = (Integer) item.get("productId");
            Integer qty = (Integer) item.get("quantity");

            Product p = productMapper.selectById(productId);
            if (p != null) {
                totalCal += 250 * qty;
                totalPro += 12 * qty;
                totalFat += 8 * qty;

                itemList.append("- ").append(p.getName()).append(" (Qty: ").append(qty).append(")\n");
            }
        }

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
            String aiContent = callLongCatApi(systemPrompt, "Please analyze my cart.");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            result.put("healthScore", contentNode.path("healthScore").asInt(80));
            result.put("aiComment", contentNode.path("aiComment").asText("Looking good, but could use more veggies!"));
            result.put("totalCalories", contentNode.path("totalCalories").asInt(totalCal));
            result.put("totalProtein", contentNode.path("totalProtein").asInt(totalPro));
            result.put("totalFat", contentNode.path("totalFat").asInt(totalFat));

            ArrayNode adviceNode = (ArrayNode) contentNode.path("adviceList");
            if (adviceNode != null) {
                for (JsonNode aNode : adviceNode) {
                    adviceList.add(aNode.asText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AI Dietitian generated an error, falling back to default.");
            result.put("healthScore", 65);
            result.put("aiComment", "Unable to reach the dietitian, so I'll just say: drink more water!");
            result.put("totalCalories", totalCal);
            result.put("totalProtein", totalPro);
            result.put("totalFat", totalFat);
            adviceList.clear();
            adviceList.add("Everything in moderation.");
            adviceList.add("Stay hydrated!");
        }

        if (adviceList.isEmpty()) {
            adviceList.add("Try adding a salad or swapping out sugary drinks.");
        }

        result.put("adviceList", adviceList);

        return result;
    }


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

    /**
     * 后台店长助手：仅 system 角色不同，通信仍在 {@link #callLongCatApi}。
     *
     * @param prompt 管理员输入的经营类问题
     */
    @Override
    public String adminChat(String prompt) throws Exception {
        String systemPrompt = "You are an AI Retail Store Manager Assistant. Answer the user briefly and professionally. You can help analyze data, draft marketing emails, or give store advice.";
        return callLongCatApi(systemPrompt, prompt);
    }

}