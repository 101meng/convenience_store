package com.lin101.store.service;
import java.util.Map;

/** 管理端仪表盘聚合（实现见 {@link com.lin101.store.service.impl.AdminServiceImpl}；REST 多在 {@link com.lin101.store.controller.AdminController}）。 */
public interface AdminService {
    Map<String, Object> getDashboardStats();
    /** @param days 近若干天的营收曲线，如 7 / 30 */
    Map<String, Object> getDashboardCharts(Integer days);
}