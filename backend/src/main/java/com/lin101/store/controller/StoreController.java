package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.entity.Category;
import com.lin101.store.entity.Product;
import com.lin101.store.service.CategoryService;
import com.lin101.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商城核心控制器
 * 处理商品分类、商品查询等商城基础业务接口请求
 * 接口基础路径：/api
 */
@RestController
@RequestMapping("/api")
public class StoreController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        try {
            return Result.success(ResultCode.SUCCESS, categoryService.list());
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/products")
    public Result<List<Product>> getProducts(@RequestParam(required = false) Integer categoryId) {
        try {
            return Result.success(ResultCode.SUCCESS, productService.getProductsByCategoryId(categoryId));
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/products/{id}")
    public Result<Product> getProductById(@PathVariable("id") Integer id) {
        try {
            return Result.success(ResultCode.SUCCESS, productService.getById(id));
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }
}