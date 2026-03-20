package com.mental.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 大学生心理健康关怀与预约平台 - 启动类
 *
 * @author Mental Health Team
 * @since 2024
 */
@SpringBootApplication
@MapperScan("com.mental.health.mapper")
public class MentalHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentalHealthApplication.class, args);
        System.out.println("========================================");
        System.out.println("心理健康平台后端服务启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
