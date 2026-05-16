package com.lin101.store.config;

import com.lin101.store.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC：便利店后端 API 以 {@code /api} 为前缀。
 * 需要登录的接口（购物车、订单、用户资料）走 {@link com.lin101.store.interceptor.JwtInterceptor}；
 * 登录认证、首页聚合、分类与商品浏览等路径排除校验。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/cart/**", "/api/order/**", "/api/user/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/home/**",
                        "/api/categories",
                        "/api/products/**"
                );
    }
}