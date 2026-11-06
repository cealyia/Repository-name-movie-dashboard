package com.ctgu.movie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 文档配置。
 * 启动后访问 http://localhost:8080/swagger-ui.html 可以看到全部接口并直接调试。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI movieDashboardOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("电影数据分析看板 API")
                .description("《Web开发技术》结课项目 - 基于 MovieLens 公开数据集的电影评分数据看板接口文档")
                .version("1.0.0")
                .contact(new Contact().name("CTGU Web开发技术课程项目")));
    }
}
