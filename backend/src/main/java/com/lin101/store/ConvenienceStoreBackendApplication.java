package com.lin101.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lin101.store.mapper")
public class ConvenienceStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvenienceStoreBackendApplication.class, args);
    }

}
