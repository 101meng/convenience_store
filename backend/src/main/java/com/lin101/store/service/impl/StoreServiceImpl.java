package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Store;
import com.lin101.store.mapper.StoreMapper;
import com.lin101.store.service.StoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    // 这里可以使用 MyBatis-Plus 默认提供的 list() 方法获取所有门店
}