package com.wanli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 万里教育平台后端服务启动类
 * 
 * @author JamesWu
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class WanliBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WanliBackendApplication.class, args);
    }
}