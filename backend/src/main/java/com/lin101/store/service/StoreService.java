package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Store;

/** 自提门店（表 {@code stores}），前台 {@link com.lin101.store.controller.StoreController#getStores} 全量列出。 */
public interface StoreService extends IService<Store> {
}