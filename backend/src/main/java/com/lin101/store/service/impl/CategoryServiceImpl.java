package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Category;
import com.lin101.store.mapper.CategoryMapper;
import com.lin101.store.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * {@link com.lin101.store.service.CategoryService} 默认实现：无额外自定义方法，
 * {@link com.lin101.store.controller.StoreController#getCategories} 使用 {@link #list()} 返回全部分类。
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    // 无覆盖方法：对外能力即 BaseMapper/ServiceImpl 的 list、getById 等；与「空实现」不同，继承链已带 CRUD
}