package com.lin101.store.config;

import com.lin101.store.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 【需要拦截验证的敏感接口】：购物车、订单、修改个人资料
                .addPathPatterns("/api/cart/**", "/api/order/**", "/api/user/**")
                // 【对所有人开放的公开接口】：登录发验证码、看首页、看商品列表
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/home/**",
                        "/api/categories",
                        "/api/products/**"
                );
    }
}