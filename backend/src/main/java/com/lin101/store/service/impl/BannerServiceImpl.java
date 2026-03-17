package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.Banner;
import com.lin101.store.mapper.BannerMapper;
import com.lin101.store.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getActiveBanners() {
        // 业务逻辑（条件装配）沉淀在 Service 层
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1).orderByDesc("sort_order");
        return this.list(queryWrapper);
    }
}