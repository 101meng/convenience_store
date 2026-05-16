package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Category;

/** 商品分类（表 {@code categories}），前台 {@link com.lin101.store.controller.StoreController#getCategories} 使用。 */
public interface CategoryService extends IService<Category> {
}