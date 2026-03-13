package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Product;
import java.util.List;

public interface ProductService extends IService<Product> {
    // 根据分类ID获取商品
    List<Product> getProductsByCategoryId(Integer categoryId);
}