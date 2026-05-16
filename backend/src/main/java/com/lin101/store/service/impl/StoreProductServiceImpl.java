package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.StoreProduct;
import com.lin101.store.mapper.StoreProductMapper;
import com.lin101.store.service.StoreProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StoreProductServiceImpl extends ServiceImpl<StoreProductMapper, StoreProduct> implements StoreProductService {

    @Autowired
    private StoreProductMapper storeProductMapper;

    @Override
    public List<Map<String, Object>> getStoreProductsWithInfo(Integer storeId) {
        return storeProductMapper.getStoreProductsWithInfo(storeId);
    }
}
