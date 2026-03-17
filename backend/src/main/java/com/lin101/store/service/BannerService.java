package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.Banner;
import java.util.List;

public interface BannerService extends IService<Banner> {
    // 新增业务方法：获取所有需要展示的广告图
    List<Banner> getActiveBanners();
}