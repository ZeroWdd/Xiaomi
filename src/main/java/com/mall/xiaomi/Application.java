package com.mall.xiaomi;

import com.mall.xiaomi.util.IdWorker;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;

/**
 * @Auther: wdd
 * @Date: 2020-03-19 15:43
 * @Description:
 */
@SpringBootApplication
@MapperScan("com.mall.xiaomi.mapper")
@EnableScheduling
public class Application {
        public static void main(String[] args) {
                SpringApplication.run(Application.class, args);
        }

        @Bean
        public IdWorker getIdWork() {
                return new IdWorker();
        }

}
