package com.lin101.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin101.store.common.AdminRole;
import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.entity.Banner;
import com.lin101.store.entity.Category;
import com.lin101.store.entity.Order;
import com.lin101.store.entity.Product;
import com.lin101.store.entity.Store;
import com.lin101.store.entity.StoreProduct;
import com.lin101.store.entity.User;
import com.lin101.store.interceptor.AdminJwtInterceptor;
import com.lin101.store.mapper.OrderMapper;
import com.lin101.store.service.BannerService;
import com.lin101.store.service.CategoryService;
import com.lin101.store.service.OrderService;
import com.lin101.store.service.ProductService;
import com.lin101.store.service.StoreProductService;
import com.lin101.store.service.StoreService;
import com.lin101.store.service.UserService;
import com.lin101.store.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreProductService storeProductService;

    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> getDashboardStats(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();

            QueryWrapper<Order> completedOrdersQuery = new QueryWrapper<Order>().eq("status", "completed");
            QueryWrapper<Order> totalOrdersQuery = new QueryWrapper<>();
            QueryWrapper<Order> todayOrdersQuery = new QueryWrapper<Order>().ge("created_at", todayStart);

            if (!isBrandAdmin(adminRole)) {
                if (adminStoreId == null) {
                    return forbidden();
                }
                completedOrdersQuery.eq("store_id", adminStoreId);
                totalOrdersQuery.eq("store_id", adminStoreId);
                todayOrdersQuery.eq("store_id", adminStoreId);
            }

            List<Order> completedOrders = orderService.list(completedOrdersQuery);
            double totalRevenue = 0;
            double todayRevenue = 0;
            for (Order order : completedOrders) {
                totalRevenue += order.getActualAmount();
                if (order.getCreatedAt() != null && !order.getCreatedAt().isBefore(todayStart)) {
                    todayRevenue += order.getActualAmount();
                }
            }

            double pastRevenue = totalRevenue - todayRevenue;
            double revenueGrowth = pastRevenue == 0 ? (todayRevenue > 0 ? 100.0 : 0.0) : (todayRevenue / pastRevenue) * 100;

            long totalOrders = orderService.count(totalOrdersQuery);
            long todayOrders = orderService.count(todayOrdersQuery);
            long pastOrders = totalOrders - todayOrders;
            double ordersGrowth = pastOrders == 0 ? (todayOrders > 0 ? 100.0 : 0.0) : ((double) todayOrders / pastOrders) * 100;

            stats.put("totalRevenue", String.format("%.2f", totalRevenue));
            stats.put("revenueGrowth", Double.valueOf(String.format("%.1f", revenueGrowth)));
            stats.put("totalOrders", totalOrders);
            stats.put("ordersGrowth", Double.valueOf(String.format("%.1f", ordersGrowth)));
            stats.put("newUsers", isBrandAdmin(adminRole) ? userService.count() : countDistinctUsersByStore(adminStoreId));
            stats.put(
                    "activeBanners",
                    isBrandAdmin(adminRole)
                            ? bannerService.count(new QueryWrapper<Banner>().eq("is_active", 1))
                            : storeProductService.count(
                                    new QueryWrapper<StoreProduct>()
                                            .eq("store_id", adminStoreId)
                                            .eq("status", 1)
                            )
            );

            return Result.success(ResultCode.SUCCESS, stats);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/dashboard/charts")
    public Result<Map<String, Object>> getDashboardCharts(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @RequestParam(defaultValue = "7") Integer days) {
        try {
            if (!isBrandAdmin(adminRole) && adminStoreId == null) {
                return forbidden();
            }

            Map<String, Object> result = new HashMap<>();
            List<String> dates = new ArrayList<>();
            List<Double> revenues = new ArrayList<>();
            LocalDate today = LocalDate.now();
            int offset = Math.max(days - 1, 0);

            QueryWrapper<Order> recentOrdersQuery = new QueryWrapper<Order>()
                    .eq("status", "completed")
                    .ge("created_at", today.minusDays(offset).atStartOfDay());
            if (!isBrandAdmin(adminRole)) {
                recentOrdersQuery.eq("store_id", adminStoreId);
            }

            List<Order> recentOrders = orderService.list(recentOrdersQuery);
            for (int i = offset; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                double dailyRevenue = recentOrders.stream()
                        .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDate().equals(date))
                        .mapToDouble(Order::getActualAmount)
                        .sum();
                revenues.add(Double.valueOf(String.format("%.2f", dailyRevenue)));
            }

            Map<String, Object> revenueTrend = new HashMap<>();
            revenueTrend.put("dates", dates);
            revenueTrend.put("revenues", revenues);
            result.put("revenueTrend", revenueTrend);
            result.put("salesDistribution", buildSalesDistribution(adminRole, adminStoreId));
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/banners")
    public Result<List<Banner>> getAllBanners(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            return Result.success(ResultCode.SUCCESS, bannerService.list());
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/banners")
    public Result<Void> addBanner(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Banner banner) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            bannerService.save(banner);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/banners")
    public Result<Void> updateBanner(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Banner banner) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            bannerService.updateById(banner);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/banners/status")
    public Result<Void> updateBannerStatus(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Map<String, Integer> requestData) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            Banner banner = new Banner();
            banner.setId(requestData.get("id"));
            banner.setIsActive(requestData.get("isActive"));
            bannerService.updateById(banner);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @PathVariable("id") Integer id) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            bannerService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/products")
    public Result<Map<String, Object>> getProductsByPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        try {
            Page<Product> page = new Page<>(current, size);
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            if (categoryId != null) {
                queryWrapper.eq("category_id", categoryId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper.like("name", keyword).or().like("description", keyword));
            }
            queryWrapper.orderByDesc("product_id");

            IPage<Product> productPage = productService.page(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("records", productPage.getRecords());
            result.put("total", productPage.getTotal());
            result.put("current", productPage.getCurrent());
            result.put("size", productPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/products")
    public Result<Void> addProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Product product) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            productService.save(product);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/products")
    public Result<Void> updateProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Product product) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            productService.updateById(product);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @PathVariable("id") Integer id) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            productService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/users")
    public Result<Map<String, Object>> getUsersByPage(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            Page<User> page = new Page<>(current, size);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper.like("nickname", keyword).or().like("phone", keyword));
            }
            queryWrapper.orderByDesc("user_id");

            IPage<User> userPage = userService.page(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("records", userPage.getRecords());
            result.put("total", userPage.getTotal());
            result.put("current", userPage.getCurrent());
            result.put("size", userPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/users")
    public Result<Void> addUser(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody User user) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
                String initial = user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname().substring(0, 1) : "U";
                user.setAvatarUrl("https://ui-avatars.com/api/?name=" + initial + "&background=random");
            }
            if (user.getBalance() == null) {
                user.setBalance(new BigDecimal("0.00"));
            }
            userService.save(user);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/users/{id}/recharge")
    public Result<Void> rechargeUser(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Object> request) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            User user = userService.getById(id);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            user.setBalance(user.getBalance().add(amount));
            userService.updateById(user);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrdersByPage(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Page<Order> page = new Page<>(current, size);
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            if (!isBrandAdmin(adminRole)) {
                if (adminStoreId == null) {
                    return forbidden();
                }
                queryWrapper.eq("store_id", adminStoreId);
            }
            if (status != null && !"All".equals(status)) {
                queryWrapper.eq("status", status.toLowerCase());
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                queryWrapper.ge("created_at", startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                queryWrapper.le("created_at", endDate + " 23:59:59");
            }
            queryWrapper.orderByDesc("created_at");

            IPage<Order> orderPage = orderService.page(page, queryWrapper);
            List<OrderVO> voList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
            for (Order order : orderPage.getRecords()) {
                OrderVO vo = new OrderVO();
                vo.setOrderId(order.getOrderId());
                vo.setOrderSn(order.getOrderSn());
                vo.setActualAmount(order.getActualAmount());
                vo.setStatus(order.getStatus().toUpperCase());
                vo.setOrderType(order.getOrderType());
                vo.setDeliveryAddress(order.getDeliveryAddress());
                if (order.getCreatedAt() != null) {
                    vo.setCreatedAt(order.getCreatedAt().format(formatter));
                }
                vo.setItems(orderMapper.getOrderItemsWithProductInfo(order.getOrderId()));
                voList.add(vo);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("records", voList);
            result.put("total", orderPage.getTotal());
            result.put("current", orderPage.getCurrent());
            result.put("size", orderPage.getSize());
            return Result.success(ResultCode.SUCCESS, result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/orders/{id}/process")
    public Result<Void> processOrder(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @PathVariable("id") Integer id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                throw new RuntimeException("Order not found");
            }
            if (!isBrandAdmin(adminRole) && !Objects.equals(order.getStoreId(), adminStoreId)) {
                return forbidden();
            }

            if ("pending".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("delivering");
            } else if ("delivering".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("completed");
            }
            orderService.updateById(order);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/categories")
    public Result<Void> addCategory(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Category category) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            categoryService.save(category);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @PathVariable("id") Integer id) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            long count = productService.count(new QueryWrapper<Product>().eq("category_id", id));
            if (count > 0) {
                throw new RuntimeException("Category not empty");
            }
            categoryService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/ai/chat")
    public Result<String> adminAiChat(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return Result.failed(ResultCode.VALIDATE_FAILED);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "LongCat-Flash-Chat");
            requestBody.put("temperature", 0.7);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", "You are an AI Retail Store Manager Assistant for 'Bento Box'. Answer the user briefly and professionally. You can help analyze data, draft marketing emails, or give store advice.");
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(sysMsg);
            messages.add(userMsg);
            requestBody.put("messages", messages);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer ak_2nY9d21Xa7PM9Wd5HD4vH48q6fY8g");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.longcat.chat/openai/v1/chat/completions",
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            return Result.success(
                    ResultCode.SUCCESS,
                    rootNode.path("choices").get(0).path("message").path("content").asText()
            );
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/stores")
    public Result<List<Store>> getStores(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId) {
        try {
            if (isBrandAdmin(adminRole)) {
                return Result.success(ResultCode.SUCCESS, storeService.list());
            }
            if (adminStoreId == null) {
                return forbidden();
            }
            List<Store> stores = new ArrayList<>();
            Store store = storeService.getById(adminStoreId);
            if (store != null) {
                stores.add(store);
            }
            return Result.success(ResultCode.SUCCESS, stores);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/stores")
    public Result<Void> addStore(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestBody Store store) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            storeService.save(store);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/stores/{id}")
    public Result<Void> updateStore(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @PathVariable Integer id,
            @RequestBody Store store) {
        try {
            if (!isBrandAdmin(adminRole) && !Objects.equals(adminStoreId, id)) {
                return forbidden();
            }
            store.setStoreId(id);
            storeService.updateById(store);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/stores/{id}")
    public Result<Void> deleteStore(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @PathVariable Integer id) {
        try {
            if (!isBrandAdmin(adminRole)) {
                return forbidden();
            }
            storeService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/store-products")
    public Result<List<Map<String, Object>>> getStoreProducts(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @RequestParam Integer storeId) {
        try {
            Integer scopedStoreId = resolveStoreScope(adminRole, adminStoreId, storeId);
            if (scopedStoreId == null) {
                return forbidden();
            }
            return Result.success(ResultCode.SUCCESS, storeProductService.getStoreProductsWithInfo(scopedStoreId));
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PostMapping("/store-products")
    public Result<Void> addStoreProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @RequestBody StoreProduct storeProduct) {
        try {
            Integer scopedStoreId = resolveStoreScope(adminRole, adminStoreId, storeProduct.getStoreId());
            if (scopedStoreId == null) {
                return forbidden();
            }
            storeProduct.setStoreId(scopedStoreId);
            storeProductService.save(storeProduct);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @PutMapping("/store-products/{id}")
    public Result<Void> updateStoreProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @PathVariable Integer id,
            @RequestBody StoreProduct storeProduct) {
        try {
            StoreProduct existing = storeProductService.getById(id);
            if (existing == null) {
                throw new RuntimeException("Store product not found");
            }
            Integer scopedStoreId = resolveStoreScope(adminRole, adminStoreId, existing.getStoreId());
            if (scopedStoreId == null) {
                return forbidden();
            }
            storeProduct.setId(id);
            storeProduct.setStoreId(scopedStoreId);
            if (storeProduct.getProductId() == null) {
                storeProduct.setProductId(existing.getProductId());
            }
            storeProductService.updateById(storeProduct);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @DeleteMapping("/store-products/{id}")
    public Result<Void> deleteStoreProduct(
            @RequestAttribute(AdminJwtInterceptor.ATTR_ADMIN_ROLE) String adminRole,
            @RequestAttribute(value = AdminJwtInterceptor.ATTR_ADMIN_STORE_ID, required = false) Integer adminStoreId,
            @PathVariable Integer id) {
        try {
            StoreProduct existing = storeProductService.getById(id);
            if (existing == null) {
                throw new RuntimeException("Store product not found");
            }
            Integer scopedStoreId = resolveStoreScope(adminRole, adminStoreId, existing.getStoreId());
            if (scopedStoreId == null) {
                return forbidden();
            }
            storeProductService.removeById(id);
            return Result.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    private boolean isBrandAdmin(String role) {
        return AdminRole.BRAND_ADMIN.matches(role);
    }

    private Integer resolveStoreScope(String role, Integer adminStoreId, Integer requestedStoreId) {
        if (isBrandAdmin(role)) {
            return requestedStoreId;
        }
        if (adminStoreId == null) {
            return null;
        }
        if (requestedStoreId == null || Objects.equals(adminStoreId, requestedStoreId)) {
            return adminStoreId;
        }
        return null;
    }

    private long countDistinctUsersByStore(Integer storeId) {
        return orderService.list(new QueryWrapper<Order>().eq("store_id", storeId))
                .stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    private List<Map<String, Object>> buildSalesDistribution(String adminRole, Integer adminStoreId) {
        List<Category> categories = categoryService.list();
        Map<Integer, String> categoryNameMap = new LinkedHashMap<>();
        for (Category category : categories) {
            categoryNameMap.put(category.getCategoryId(), category.getCategoryName());
        }

        Map<Integer, Long> counts = new LinkedHashMap<>();
        if (isBrandAdmin(adminRole)) {
            for (Product product : productService.list()) {
                if (product.getCategoryId() != null) {
                    counts.merge(product.getCategoryId(), 1L, Long::sum);
                }
            }
        } else if (adminStoreId != null) {
            for (Map<String, Object> storeProduct : storeProductService.getStoreProductsWithInfo(adminStoreId)) {
                Integer categoryId = parseInteger(storeProduct.get("category_id"));
                if (categoryId != null) {
                    counts.merge(categoryId, 1L, Long::sum);
                }
            }
        }

        List<Map<String, Object>> distribution = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            String categoryName = categoryNameMap.get(entry.getKey());
            if (categoryName == null) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("name", categoryName);
            item.put("value", entry.getValue());
            distribution.add(item);
        }
        return distribution;
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private <T> Result<T> forbidden() {
        return Result.failed(ResultCode.FORBIDDEN);
    }
}
