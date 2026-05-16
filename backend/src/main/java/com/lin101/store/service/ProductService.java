package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Product;
import com.lin101.store.vo.ProductVO;
import java.util.List;

public interface ProductService extends IService<Product> {
    List<Product> getProductsByCategoryId(Integer categoryId);
    List<Product> getFlashSaleProducts();
    List<ProductVO> getStoreNewArrivals(Integer storeId);
    List<ProductVO> getStoreProducts(Integer storeId, Integer categoryId);
    List<ProductVO> getStoreFlashSaleProducts(Integer storeId);
    ProductVO getStoreProductById(Integer storeId, Integer productId);
}