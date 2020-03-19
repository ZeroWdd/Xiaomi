package com.mall.xiaomi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 15:43
 * @Description:
 */
@SpringBootApplication
@MapperScan("com.mall.xiaomi.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
