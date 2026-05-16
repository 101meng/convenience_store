package com.lin101.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 便利店电商后端（Spring Boot）：商品、购物车、订单、用户与管理员接口；
 * MyBatis Mapper 扫描包 {@code com.lin101.store.mapper}。
 */
@SpringBootApplication
@MapperScan("com.lin101.store.mapper")
public class ConvenienceStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvenienceStoreBackendApplication.class, args);
    }

}
