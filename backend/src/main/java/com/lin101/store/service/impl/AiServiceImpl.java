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

/**
 * LongCat Chat Completions 封装。核心流程：拼 system/user messages → POST → 取 {@code choices[0].message.content}
 * 当作<strong>一段字符串</strong>再 {@link ObjectMapper#readTree(String)}；因此模型必须返回<strong>合法 JSON 文本</strong>。
 */
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private ProductMapper productMapper;

    /** LongCat API 地址；生产环境建议移到配置中心并轮换密钥。 */
    private static final String API_URL = "https://api.longcat.chat/openai/v1/chat/completions";
    /** 与本项目演示账号绑定；切勿提交到公开仓库时可改为环境变量。 */
    private static final String API_KEY = "ak_2nY9d21Xa7PM9Wd5HD4vH48q6fY8g";

    /**
     * 首页「AI 搭配」：用户输入场景描述，模型从全库商品中选 2～3 个 ID。
     *
     * @param prompt 用户自然语言需求（英文提示词要求模型英文回复）
     * @return {@code aiMessage} 文案 + {@code recommendedProducts} 实体列表（按模型给出的 ID 查询）
     */
    @Override
    public Map<String, Object> generateSmartCombo(String prompt) {
        // 全表扫描构造「ID: 名称」清单，保证模型只推荐真实存在的 product_id
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

        // 默认值：接口失败或解析失败时仍返回可读文案，避免前端空白
        String finalAiMessage = "Seems like a network glitch! But here is a fresh combo for you anyway 🥗";
        List<Integer> recommendedProductIds = new ArrayList<>();

        try {
            String aiContent = callLongCatApi(systemPrompt, prompt);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            // path 不会 NPE；缺失时 asText() 为 ""，会覆盖上面的默认英文文案
            finalAiMessage = contentNode.path("aiMessage").asText();
            // 若模型返回非数组或字段缺失，此处强转可能抛异常 → 进入 catch
            ArrayNode idsNode = (ArrayNode) contentNode.path("recommendedProductIds");
            for (JsonNode idNode : idsNode) {
                recommendedProductIds.add(idNode.asInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("AI Smart Combo generated an error, falling back to default.");
            // 演示用固定 ID，需与数据库中真实商品一致
            recommendedProductIds.clear();
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        // 解析成功但数组为空：同样 fallback，避免 IN () 或前端无商品
        if (recommendedProductIds.isEmpty()) {
            recommendedProductIds.add(1);
            recommendedProductIds.add(11);
        }

        // MyBatis-Plus IN 查询；返回列表顺序与 ID 列表不一定一致
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id", recommendedProductIds);
        List<Product> products = productMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("aiMessage", finalAiMessage);
        result.put("recommendedProducts", products);

        return result;
    }

    /**
     * 购物车「营养师」：先按行汇总（演示倍率），再把摘要与商品清单写入 system 提示词，解析模型返回的评分与 {@code adviceList}。
     *
     * @param cartItems 前端传入的多行，键名约定 {@code productId}、{@code quantity}（见客户端契约）
     * @return 含 {@code healthScore}、{@code aiComment}、{@code adviceList}、三大营养素等，供原生/Android 直接渲染
     */
    @Override
    public Map<String, Object> analyzeNutrition(List<Map<String, Object>> cartItems) {
        int totalCal = 0, totalPro = 0, totalFat = 0;
        StringBuilder itemList = new StringBuilder();

        // 调用方若传 null 会在迭代处 NPE；此处不防御，与 Controller 契约一致
        for (Map<String, Object> item : cartItems) {
            Integer productId = (Integer) item.get("productId");
            Integer qty = (Integer) item.get("quantity");

            Product p = productMapper.selectById(productId);
            if (p != null) {
                // 演示：未读 Product.calories 等列，固定倍率乘数量；接入真实数据时改为 p.getCalories() * qty
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

            // 第二参数为 JsonNode 缺失时的默认值；total* 与上面循环累计保持一致优先
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

        // 模型偶发返回空数组：补一条可用建议，避免客户端列表为空
        if (adviceList.isEmpty()) {
            adviceList.add("Try adding a salad or swapping out sugary drinks.");
        }

        result.put("adviceList", adviceList);

        return result;
    }

    /**
     * 组装 OpenAI 兼容请求 POST 到 {@link #API_URL}，返回助手回复中的纯文本 content。
     * 部分模型仍包裹 {@code ```json }，此处正则剥离以免影响 {@link ObjectMapper#readTree}。
     *
     * @param systemPrompt 角色与输出格式约束
     * @param userPrompt   用户侧一句话（场景推荐或营养分析中的固定提示）
     */
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

        // 每次新建 RestTemplate，短连接场景可接受；高并发应注入单例 Bean
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        // OpenAI 兼容结构：choices[0].message.content
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
        String systemPrompt = "You are an AI Retail Store Manager Assistant for 'Bento Box'. Answer the user briefly and professionally. You can help analyze data, draft marketing emails, or give store advice.";
        return callLongCatApi(systemPrompt, prompt);
    }

}