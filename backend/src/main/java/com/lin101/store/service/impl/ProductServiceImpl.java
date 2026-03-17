package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Product;
import com.lin101.store.mapper.ProductMapper;
import com.lin101.store.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    // 实现我们在接口中定义的业务方法
    @Override
    public List<Product> getProductsByCategoryId(Integer categoryId) {
        if (categoryId == null) {
            return this.list(); // MyBatis-Plus 提供的查所有方法
        }
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> getFlashSaleProducts() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        // 条件：是秒杀商品 (is_flash_sale = 1)，并且结束时间大于当前系统时间
        queryWrapper.eq("is_flash_sale", 1).gt("flash_sale_end_time", LocalDateTime.now());
        return this.list(queryWrapper);
    }
}