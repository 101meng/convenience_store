package com.lin101.store.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件：本项目数据库为 MySQL（数据源配置见 {@code src/main/resources/application.properties}）。
 * {@link PaginationInnerInterceptor} 需指定 {@link DbType#MYSQL}，分页 SQL 方言与线上库一致。
 * 若后续在同一拦截器链中注册多个 InnerInterceptor，分页插件宜放在最后注册。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}