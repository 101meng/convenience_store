package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Banner;
import java.util.List;

/** 首页轮播：在 {@link com.lin101.store.service.impl.BannerServiceImpl} 中过滤启用状态与排序。 */
public interface BannerService extends IService<Banner> {
    List<Banner> getActiveBanners();
}