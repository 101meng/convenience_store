package com.lin101.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.entity.Product;
import com.lin101.store.service.BannerService;
import com.lin101.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private ProductService productService;

    @GetMapping("/index")
    public Result<Map<String, Object>> getHomeData() {
        try {
            Map<String, Object> homeData = new HashMap<>();

            // 1. 装载轮播广告
            homeData.put("banners", bannerService.getActiveBanners());

            // 2. 装载秒杀商品
            homeData.put("flashSales", productService.getFlashSaleProducts());

            // 3. 【核心补全】：装载新品推荐（按 ID 倒序取最新 10 个）
            QueryWrapper<Product> newArrivalsQuery = new QueryWrapper<>();
            newArrivalsQuery.orderByDesc("product_id").last("LIMIT 10");
            homeData.put("newArrivals", productService.list(newArrivalsQuery));

            return Result.success(ResultCode.SUCCESS, homeData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }
}