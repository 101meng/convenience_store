package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Banner;
import com.lin101.store.mapper.BannerMapper;
import com.lin101.store.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link com.lin101.store.service.BannerService} 实现：表 {@code banners}；
 * 首页轮播仅展示启用记录，{@code sort_order} 越大越靠前。
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    /**
     * @return 仅 {@code is_active = 1}，供 {@link com.lin101.store.controller.HomeController} 使用
     */
    @Override
    public List<Banner> getActiveBanners() {
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        // orderByDesc(sort_order)：数值大的排前面（权重高的先展示）
        queryWrapper.eq("is_active", 1).orderByDesc("sort_order");
        return this.list(queryWrapper);
    }
}