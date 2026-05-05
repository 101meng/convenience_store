package com.lin101.store.service;
import java.util.Map;

public interface AdminService {
    Map<String, Object> getDashboardStats();
    // 🔥 新增 days 参数
    Map<String, Object> getDashboardCharts(Integer days);
}