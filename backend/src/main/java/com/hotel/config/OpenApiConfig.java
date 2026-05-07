package com.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置
 */
@Configuration
public class OpenApiConfig {

    /**
     * 自定义OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("酒店管理系统API")
                        .version("1.0.0")
                        .description("酒店管理系统后端接口文档")
                        .contact(new Contact()
                                .name("Hotel Management Team")
                                .email("support@hotel.com")));
    }
}
