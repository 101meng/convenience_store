package com.lin101.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.entity.Banner;
import com.lin101.store.entity.Category;
import com.lin101.store.entity.Order;
import com.lin101.store.entity.Product;
import com.lin101.store.entity.User;
import com.lin101.store.mapper.OrderMapper;
import com.lin101.store.service.BannerService;
import com.lin101.store.service.CategoryService;
import com.lin101.store.service.OrderService;
import com.lin101.store.service.ProductService;
import com.lin101.store.service.UserService;
import com.lin101.store.vo.OrderVO;
import com.lin101.store.entity.Store;
import com.lin101.store.entity.StoreProduct;
import com.lin101.store.service.StoreService;
import com.lin101.store.service.StoreProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 绠＄悊绔?REST锛堣矾寰勫墠缂€ {@code /api/admin}锛夈€傝仛鍚堜华琛ㄧ洏缁熻銆丅anner/鍟嗗搧/鐢ㄦ埛/璁㈠崟/鍒嗙被鐨?CRUD锛?
 * 浠ュ強鍚庡彴鐩磋繛 LongCat 鐨勫璇濇帴鍙ｏ紙涓?{@link com.lin101.store.service.impl.AiServiceImpl} 浣跨敤鍚屼竴妯″瀷绾胯矾锛夈€?
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ProductService productService;
    @Autowired private BannerService bannerService;
    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private OrderMapper orderMapper;
    @Autowired private CategoryService categoryService;

    /** 鎬昏惀鏀躲€佷粖鏃ュ姣斿骞呫€佽鍗曟暟銆佺敤鎴锋暟銆佸惎鐢?Banner 鏁扮瓑鎸囨爣銆?*/
    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            java.time.LocalDateTime todayStart = java.time.LocalDate.now().atStartOfDay();

            List<Order> completedOrders = orderService.list(new QueryWrapper<Order>().eq("status", "completed"));
            double totalRevenue = 0, todayRevenue = 0;
            for (Order o : completedOrders) {
                totalRevenue += o.getActualAmount();
                if (o.getCreatedAt() != null && !o.getCreatedAt().isBefore(todayStart)) todayRevenue += o.getActualAmount();
            }
            double pastRevenue = totalRevenue - todayRevenue;
            double revenueGrowth = pastRevenue == 0 ? (todayRevenue > 0 ? 100.0 : 0.0) : (todayRevenue / pastRevenue) * 100;

            long totalOrders = orderService.count();
            long todayOrders = orderService.count(new QueryWrapper<Order>().ge("created_at", todayStart));
            long pastOrders = totalOrders - todayOrders;
            double ordersGrowth = pastOrders == 0 ? (todayOrders > 0 ? 100.0 : 0.0) : ((double) todayOrders / pastOrders) * 100;

            stats.put("totalRevenue", String.format("%.2f", totalRevenue));
            stats.put("revenueGrowth", Double.valueOf(String.format("%.1f", revenueGrowth)));
            stats.put("totalOrders", totalOrders);
            stats.put("ordersGrowth", Double.valueOf(String.format("%.1f", ordersGrowth)));
            stats.put("newUsers", userService.count());
            stats.put("activeBanners", bannerService.count(new QueryWrapper<Banner>().eq("is_active", 1)));

            return Result.success(ResultCode.SUCCESS, stats);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /**
     * 鍥捐〃鏁版嵁锛氳繎 {@code days} 澶╂瘡鏃ュ凡瀹屾垚璁㈠崟钀ユ敹鏇茬嚎锛涗互鍙婃寜鍒嗙被缁熻鐨勫晢鍝佹暟閲忓垎甯冦€?
     */
    @GetMapping("/dashboard/charts")
    public Result<Map<String, Object>> getDashboardCharts(@RequestParam(defaultValue = "7") Integer days) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<String> dates = new ArrayList<>();
            List<Double> revenues = new ArrayList<>();
            LocalDate today = LocalDate.now();

            int offset = days - 1;

            List<Order> recentOrders = orderService.list(new QueryWrapper<Order>()
                    .eq("status", "completed")
                    .ge("created_at", today.minusDays(offset).atStartOfDay()));

            for (int i = offset; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                double dailyRevenue = recentOrders.stream()
                        .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(date))
                        .mapToDouble(Order::getActualAmount).sum();
                revenues.add(Double.valueOf(String.format("%.2f", dailyRevenue)));
            }
            Map<String, Object> revenueTrend = new HashMap<>();
            revenueTrend.put("dates", dates);
            revenueTrend.put("revenues", revenues);
            result.put("revenueTrend", revenueTrend);

            List<Category> categories = categoryService.list();
            List<Product> products = productService.list();
            List<Map<String, Object>> distribution = new ArrayList<>();
            for (Category cat : categories) {
                long count = products.stream().filter(p -> p.getCategoryId().equals(cat.getCategoryId())).count();
                if (count > 0) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", cat.getCategoryName()); item.put("value", count); distribution.add(item);
                }
            }
            result.put("salesDistribution", distribution);
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** Banner 鍒楄〃锛堝惈鏈惎鐢級銆?*/
    @GetMapping("/banners")
    public Result<List<Banner>> getAllBanners() {
        try { return Result.success(ResultCode.SUCCESS, bannerService.list()); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鏂板 Banner銆?*/
    @PostMapping("/banners")
    public Result<Void> addBanner(@RequestBody Banner banner) {
        try { bannerService.save(banner); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍏ㄩ噺鏇存柊 Banner 瀛楁銆?*/
    @PutMapping("/banners")
    public Result<Void> updateBanner(@RequestBody Banner banner) {
        try { bannerService.updateById(banner); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 浠呮洿鏂颁笂涓嬬嚎锛歿@code id}銆亄@code isActive}銆?*/
    @PutMapping("/banners/status")
    public Result<Void> updateBannerStatus(@RequestBody Map<String, Integer> requestData) {
        try {
            Banner banner = new Banner(); banner.setId(requestData.get("id")); banner.setIsActive(requestData.get("isActive"));
            bannerService.updateById(banner); return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍒犻櫎 Banner銆?*/
    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable("id") Integer id) {
        try { bannerService.removeById(id); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍟嗗搧鍒嗛〉锛涙敮鎸佹寜鍒嗙被涓庡悕绉?鎻忚堪妯＄硦鎼滅储锛岃繑鍥?MyBatis-Plus 鍒嗛〉鍏冩暟鎹€?*/
    @GetMapping("/products")
    public Result<Map<String, Object>> getProductsByPage(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String keyword) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            if (categoryId != null) queryWrapper.eq("category_id", categoryId);
            if (keyword != null && !keyword.trim().isEmpty()) queryWrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
            queryWrapper.orderByDesc("product_id");
            com.baomidou.mybatisplus.core.metadata.IPage<Product> productPage = productService.page(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("records", productPage.getRecords()); result.put("total", productPage.getTotal()); result.put("current", productPage.getCurrent()); result.put("size", productPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鏂板鍟嗗搧銆?*/
    @PostMapping("/products")
    public Result<Void> addProduct(@RequestBody Product product) {
        try { productService.save(product); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鏇存柊鍟嗗搧銆?*/
    @PutMapping("/products")
    public Result<Void> updateProduct(@RequestBody Product product) {
        try { productService.updateById(product); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍒犻櫎鍟嗗搧銆?*/
    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable("id") Integer id) {
        try { productService.removeById(id); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鐢ㄦ埛鍒嗛〉锛涙敮鎸佹樀绉版垨鎵嬫満鍙锋ā绯婃煡銆?*/
    @GetMapping("/users")
    public Result<Map<String, Object>> getUsersByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(w -> w.like("nickname", keyword).or().like("phone", keyword));
            }
            queryWrapper.orderByDesc("user_id");
            com.baomidou.mybatisplus.core.metadata.IPage<User> userPage = userService.page(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("records", userPage.getRecords()); result.put("total", userPage.getTotal()); result.put("current", userPage.getCurrent()); result.put("size", userPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鏂板鐢ㄦ埛锛涙湭浼犲ご鍍忔椂鐢熸垚鍗犱綅鍥撅紝浣欓榛樿 0銆?*/
    @PostMapping("/users")
    public Result<Void> addUser(@RequestBody User user) {
        try {
            if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
                String initial = user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname().substring(0, 1) : "U";
                user.setAvatarUrl("https://ui-avatars.com/api/?name=" + initial + "&background=random");
            }
            if (user.getBalance() == null) user.setBalance(new BigDecimal("0.00"));
            userService.save(user);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鐢ㄦ埛浣欓鍏呭€硷細璇锋眰浣?{@code amount} 绱姞鍒板綋鍓嶄綑棰濄€?*/
    @PutMapping("/users/{id}/recharge")
    public Result<Void> rechargeUser(@PathVariable("id") Integer id, @RequestBody Map<String, Object> request) {
        try {
            User user = userService.getById(id);
            if (user == null) throw new RuntimeException("User not found");

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            user.setBalance(user.getBalance().add(amount));
            userService.updateById(user);

            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    /**
     * 璁㈠崟鍒嗛〉锛涘彲閫夌姸鎬併€佸垱寤烘椂闂村尯闂淬€傝褰曡浆鎹负 {@link OrderVO}锛堝惈鏄庣粏鑱旇〃锛夈€?
     */
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrdersByPage(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) String status, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (status != null && !"All".equals(status)) queryWrapper.eq("status", status.toLowerCase());
            if (startDate != null && !startDate.trim().isEmpty()) queryWrapper.ge("created_at", startDate + " 00:00:00");
            if (endDate != null && !endDate.trim().isEmpty()) queryWrapper.le("created_at", endDate + " 23:59:59");
            queryWrapper.orderByDesc("created_at");

            com.baomidou.mybatisplus.core.metadata.IPage<Order> orderPage = orderService.page(page, queryWrapper);
            List<OrderVO> voList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
            for(Order order : orderPage.getRecords()) {
                OrderVO vo = new OrderVO(); vo.setOrderId(order.getOrderId()); vo.setOrderSn(order.getOrderSn()); vo.setActualAmount(order.getActualAmount()); vo.setStatus(order.getStatus().toUpperCase()); vo.setOrderType(order.getOrderType()); vo.setDeliveryAddress(order.getDeliveryAddress());
                if (order.getCreatedAt() != null) vo.setCreatedAt(order.getCreatedAt().format(formatter));
                vo.setItems(orderMapper.getOrderItemsWithProductInfo(order.getOrderId()));
                voList.add(vo);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("records", voList); result.put("total", orderPage.getTotal()); result.put("current", orderPage.getCurrent()); result.put("size", orderPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鎺ㄨ繘璁㈠崟鐘舵€侊細{@code pending}鈫抺@code delivering}鈫抺@code completed}銆?*/
    @PutMapping("/orders/{id}/process")
    public Result<Void> processOrder(@PathVariable("id") Integer id) {
        try {
            Order order = orderService.getById(id);
            if (order != null) {
                if ("pending".equalsIgnoreCase(order.getStatus())) order.setStatus("delivering");
                else if ("delivering".equalsIgnoreCase(order.getStatus())) order.setStatus("completed");
                orderService.updateById(order);
            }
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鏂板鍒嗙被銆?*/
    @PostMapping("/categories")
    public Result<Void> addCategory(@RequestBody Category category) {
        try { categoryService.save(category); return Result.success(ResultCode.SUCCESS); }
        catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍒犻櫎鍒嗙被锛涜嫢浠嶆湁鍟嗗搧寮曠敤璇ュ垎绫诲垯澶辫触銆?*/
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Integer id) {
        try {
            long count = productService.count(new QueryWrapper<Product>().eq("category_id", id));
            if (count > 0) throw new RuntimeException("Category not empty");
            categoryService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }

    /** 鍚庡彴搴楅暱鍔╂墜锛氳姹備綋 {@code prompt}锛岀洿杩?LongCat 杩斿洖鏂囨湰銆?*/
    @PostMapping("/ai/chat")
    public Result<String> adminAiChat(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) return Result.failed(ResultCode.VALIDATE_FAILED);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "LongCat-Flash-Chat"); requestBody.put("temperature", 0.7);
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> sysMsg = new HashMap<>(); sysMsg.put("role", "system"); sysMsg.put("content", "You are an AI Retail Store Manager Assistant for 'Bento Box'. Answer the user briefly and professionally. You can help analyze data, draft marketing emails, or give store advice.");
            Map<String, String> userMsg = new HashMap<>(); userMsg.put("role", "user"); userMsg.put("content", prompt);
            messages.add(sysMsg); messages.add(userMsg); requestBody.put("messages", messages);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON); headers.set("Authorization", "Bearer ak_2nY9d21Xa7PM9Wd5HD4vH48q6fY8g");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.longcat.chat/openai/v1/chat/completions", entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            return Result.success(ResultCode.SUCCESS, rootNode.path("choices").get(0).path("message").path("content").asText());
        } catch (Exception e) { return Result.failed(ResultCode.FAILED); }
    }
    // ============================================================
    // Store Management
    // ============================================================

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreProductService storeProductService;

    @GetMapping("/stores")
    public Result<List<com.lin101.store.entity.Store>> getStores() {
        try {
            return Result.success(ResultCode.SUCCESS, storeService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/stores")
    public Result<Void> addStore(@RequestBody Store store) {
        try {
            storeService.save(store);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/stores/{id}")
    public Result<Void> updateStore(@PathVariable Integer id, @RequestBody Store store) {
        try {
            store.setStoreId(id);
            storeService.updateById(store);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/stores/{id}")
    public Result<Void> deleteStore(@PathVariable Integer id) {
        try {
            storeService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    // ============================================================
    // Store Product Management
    // ============================================================

    @GetMapping("/store-products")
    public Result<List<Map<String, Object>>> getStoreProducts(@RequestParam Integer storeId) {
        try {
            return Result.success(ResultCode.SUCCESS, storeProductService.getStoreProductsWithInfo(storeId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/store-products")
    public Result<Void> addStoreProduct(@RequestBody StoreProduct sp) {
        try {
            storeProductService.save(sp);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/store-products/{id}")
    public Result<Void> updateStoreProduct(@PathVariable Integer id, @RequestBody StoreProduct sp) {
        try {
            sp.setId(id);
            storeProductService.updateById(sp);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/store-products/{id}")
    public Result<Void> deleteStoreProduct(@PathVariable Integer id) {
        try {
            storeProductService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

}


