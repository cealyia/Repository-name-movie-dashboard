package com.ctgu.movie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 电影数据分析看板 - 启动类
 *
 * 运行方式：在项目根目录执行 mvn spring-boot:run
 * 启动后访问 http://localhost:8080
 */
@SpringBootApplication
@MapperScan("com.ctgu.movie.mapper")
public class MovieDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieDashboardApplication.class, args);
    }
}
