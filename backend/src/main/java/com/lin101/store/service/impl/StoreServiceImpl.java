package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Store;
import com.lin101.store.mapper.StoreMapper;
import com.lin101.store.service.StoreService;
import org.springframework.stereotype.Service;

/**
 * {@link com.lin101.store.service.StoreService} 默认实现：无筛选条件，
 * {@link com.lin101.store.controller.StoreController#getStores} 使用 {@link #list()} 返回全部自提门店。
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    // 同上：门店列表无额外过滤，控制器调用继承的 list()
}