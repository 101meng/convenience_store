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

/**
 * 后台仪表盘数据聚合。注意：营收相关只统计 {@code status = 'completed'} 的订单金额字段 {@link Order#getActualAmount()}；
 * {@link #getDashboardStats()} 里的「增幅」分母是「至今已完成总额减去今日已完成」，不是「昨日」。
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private BannerService bannerService;
    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;

    /**
     * KPI 计算要点：<br>
     * ① {@code totalRevenue}：所有已完成订单实付之和。<br>
     * ② {@code todayRevenue}：其中创建时间 ≥ 当日 0 点的部分。<br>
     * ③ {@code pastRevenue = totalRevenue - todayRevenue}：等价于「今日之前已完成累计」（不是昨日单日）。<br>
     * ④ {@code revenueGrowth = todayRevenue / pastRevenue * 100}：今日相对「历史已完成余额」的比例；pastRevenue=0 时若今日有营收则置 100，否则 0。<br>
     * ⑤ 订单数：{@code totalOrders} 为<strong>全部状态</strong>订单总数；{@code todayOrders} 仅按创建时间筛选；增幅公式与营收同理。<br>
     * ⑥ {@code newUsers}：实为 {@link UserService#count()} 用户表总行数，并非「当日新增」（字段名来自前端约定）。
     */
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<Order> completedOrders = orderService.list(new QueryWrapper<Order>().eq("status", "completed"));
        double totalRevenue = 0, todayRevenue = 0;
        for (Order o : completedOrders) {
            totalRevenue += o.getActualAmount();
            // createdAt 落在今日 0 点及之后算「今日营收」
            if (o.getCreatedAt() != null && !o.getCreatedAt().isBefore(todayStart)) {
                todayRevenue += o.getActualAmount();
            }
        }
        // 「今日之前」的已完成累计，用作 revenueGrowth 分母
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

    /**
     * 营收折线：窗口 [{@code today - (days-1)}, {@code today}] 共 {@code days} 个自然日，每日金额 = 当日创建的已完成订单 {@code actualAmount} 之和。<br>
     * {@code salesDistribution}：内存里按分类遍历，统计<strong>当前商品表</strong>中每个 {@code category_id} 下 SKU 条数（不是订单销量）。双层循环品类×商品。
     *
     * @param days 天数，如 7、30
     */
    @Override
    public Map<String, Object> getDashboardCharts(Integer days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 共 days 天：从今天往回 offset 天
        int offset = days - 1;

        // 一次查出窗口内已完成订单，再在内存按日聚合
        List<Order> recentOrders = orderService.list(new QueryWrapper<Order>()
                .eq("status", "completed")
                .ge("created_at", today.minusDays(offset).atStartOfDay()));

        // i 从大到小减：dates/revenues 时间轴从「窗口首日」排到「今天」
        for (int i = offset; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            // 按订单创建日落在 date 且已在 recentOrders 集合内（已完成且 created_at 在窗口内）汇总
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

        // 饼图/分布：按 category_id 统计商品条数，过滤掉 0 件的分类
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