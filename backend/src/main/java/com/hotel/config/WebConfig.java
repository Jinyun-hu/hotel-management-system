package com.hotel.config;

import com.hotel.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // 拦截所有API请求
                .excludePathPatterns(
                        "/api/auth/login",      // 排除登录接口
                        "/api/auth/register",   // 排除注册接口
                        "/api/swagger-ui/**",   // 排除Swagger UI
                        "/api/v3/api-docs/**"   // 排除API文档
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源访问路径，使用项目根目录下的 uploads 文件夹
        String uploadAbsolutePath = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath);
    }
}
