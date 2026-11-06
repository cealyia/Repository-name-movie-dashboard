package com.ctgu.movie.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web 相关配置：
 * 1) 允许跨域：前端如果放在 VS Code Live Server（http://localhost:5500）上调试，
 *    浏览器会拦截跨域请求，这里统一放开 /api/** 的本机来源。
 * 2) 把项目根目录下的 web/ 目录作为静态资源目录，这样改 HTML/CSS/JS 后
 *    刷新浏览器就能生效，不用重新打包。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 前端源码放在 src/main/webapp，按相对路径直接读取，
        // 改完 HTML/CSS/JS 刷新浏览器即可生效，不用重新打包。
        Path webDir = Paths.get("src", "main", "webapp").toAbsolutePath().normalize();
        if (Files.isDirectory(webDir)) {
            String location = webDir.toUri().toString();
            log.info("前端静态资源目录：{}", location);
            registry.addResourceHandler("/**")
                    .addResourceLocations(location)
                    .setCachePeriod(0);
        } else {
            log.warn("未找到 web/ 目录（{}），前端页面将无法访问", webDir);
        }
    }
}
