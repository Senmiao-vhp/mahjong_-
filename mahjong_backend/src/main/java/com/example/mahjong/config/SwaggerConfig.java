package com.example.mahjong.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.logging.Logger;

/**
 * SpringDoc OpenAPI 配置类
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
    
    private static final Logger logger = Logger.getLogger(SwaggerConfig.class.getName());
    
    @Bean
    public OpenAPI mahjongAPI() {
        logger.info("初始化OpenAPI配置");
        return new OpenAPI()
                .info(new Info().title("雀鬼麻将平台 API 文档")
                        .description("雀鬼麻将平台后端接口文档，提供房间管理、游戏逻辑和用户管理等API")
                        .version("1.0")
                        .contact(new Contact()
                                .name("雀鬼开发团队")
                                .url("http://mahjong.example.com")
                                .email("dev@mahjong.example.com"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目Wiki文档")
                        .url("https://github.com/yourusername/mahjong/wiki"))
                // 配置空的安全方案，禁用Basic Auth认证
                .components(new Components().securitySchemes(null));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        logger.info("创建公开API组");
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/**")
                .build();
    }
} 