package com.lin101.store.controller;

import com.lin101.store.entity.Category;
import com.lin101.store.entity.Product;
import com.lin101.store.service.CategoryService;
import com.lin101.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StoreController {

    // 现在注入的是 Service，而不是 Mapper
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryService.list(); // 直接调用 Service 的 list() 方法
    }

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) Integer categoryId) {
        // 具体的查询逻辑已经被封装在 Service 里了，Controller 变得极其干净
        return productService.getProductsByCategoryId(categoryId);
    }
}