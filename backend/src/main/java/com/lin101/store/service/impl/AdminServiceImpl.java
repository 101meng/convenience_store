package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lin101.store.entity.Banner;
import com.lin101.store.entity.Category;
import com.lin101.store.entity.Order;
import com.lin101.store.entity.Product;
import com.lin101.store.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private BannerService bannerService;
    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<Order> completedOrders = orderService.list(new QueryWrapper<Order>().eq("status", "completed"));
        double totalRevenue = 0, todayRevenue = 0;
        for (Order o : completedOrders) {
            totalRevenue += o.getActualAmount();
            if (o.getCreatedAt() != null && !o.getCreatedAt().isBefore(todayStart)) {
                todayRevenue += o.getActualAmount();
            }
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

        return stats;
    }

    @Override
    public Map<String, Object> getDashboardCharts(Integer days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 🔥 真实的动态时间倒推（7天或30天）
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
                item.put("name", cat.getCategoryName());
                item.put("value", count);
                distribution.add(item);
            }
        }
        result.put("salesDistribution", distribution);
        return result;
    }
}