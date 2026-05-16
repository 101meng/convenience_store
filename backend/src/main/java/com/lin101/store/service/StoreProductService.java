package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.StoreProduct;

import java.util.List;
import java.util.Map;

public interface StoreProductService extends IService<StoreProduct> {
    List<Map<String, Object>> getStoreProductsWithInfo(Integer storeId);
}
