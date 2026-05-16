package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Product;
import com.lin101.store.mapper.ProductMapper;
import com.lin101.store.service.ProductService;
import com.lin101.store.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getProductsByCategoryId(Integer categoryId) {
        QueryWrapper<Product> qw = new QueryWrapper<>();
        qw.eq(categoryId != null, "category_id", categoryId);
        return this.list(qw);
    }

    @Override
    public List<Product> getFlashSaleProducts() {
        return this.list(new QueryWrapper<Product>()
                .eq("is_flash_sale", 1)
                .gt("flash_sale_end_time", LocalDateTime.now()));
    }

    @Override
    public List<ProductVO> getStoreNewArrivals(Integer storeId) {
        return productMapper.getStoreNewArrivals(storeId);
    }

    @Override
    public List<ProductVO> getStoreProducts(Integer storeId, Integer categoryId) {
        return productMapper.getStoreProducts(storeId, categoryId);
    }

    @Override
    public List<ProductVO> getStoreFlashSaleProducts(Integer storeId) {
        return productMapper.getStoreFlashSaleProducts(storeId);
    }

    @Override
    public ProductVO getStoreProductById(Integer storeId, Integer productId) {
        return productMapper.getStoreProductById(storeId, productId);
    }
}