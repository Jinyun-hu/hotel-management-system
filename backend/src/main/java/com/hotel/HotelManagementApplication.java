package com.hotel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 酒店管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.hotel.mapper")
@EnableScheduling
public class HotelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementApplication.class, args);
        System.out.println("===============================================");
        System.out.println("酒店管理系统启动成功!");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("===============================================");
    }
}
