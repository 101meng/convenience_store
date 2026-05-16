package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.BannerService;
import com.lin101.store.service.ProductService;
import com.lin101.store.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private ProductService productService;

    @GetMapping("/index")
    public Result<Map<String, Object>> getHomeData(
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            Map<String, Object> homeData = new HashMap<>();

            homeData.put("banners", bannerService.getActiveBanners());

            List<ProductVO> storeFlashSales = productService.getStoreFlashSaleProducts(storeId);
            homeData.put("flashSales", storeFlashSales);

            homeData.put("newArrivals", productService.getStoreNewArrivals(storeId));

            return Result.success(ResultCode.SUCCESS, homeData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }
}