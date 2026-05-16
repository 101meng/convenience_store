package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.entity.Category;
import com.lin101.store.entity.Store;
import com.lin101.store.service.CategoryService;
import com.lin101.store.service.ProductService;
import com.lin101.store.service.StoreService;
import com.lin101.store.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StoreController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        try {
            return Result.success(ResultCode.SUCCESS, categoryService.list());
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/products")
    public Result<?> getProducts(
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId,
            @RequestParam(required = false) Integer categoryId) {
        try {
            return Result.success(ResultCode.SUCCESS, productService.getStoreProducts(storeId, categoryId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/products/{id}")
    public Result<ProductVO> getProductById(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-Store-Id", defaultValue = "1") Integer storeId) {
        try {
            ProductVO product = productService.getStoreProductById(storeId, id);
            if (product == null) {
                return Result.failed(ResultCode.PRODUCT_NOT_FOUND);
            }
            return Result.success(ResultCode.SUCCESS, product);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/stores")
    public Result<List<Store>> getStores() {
        try {
            return Result.success(ResultCode.SUCCESS, storeService.list());
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }
}