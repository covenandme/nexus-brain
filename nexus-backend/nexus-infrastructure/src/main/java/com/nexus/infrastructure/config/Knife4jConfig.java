package com.nexus.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置类
 * 用于配置 API 文档生成
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置全局 OpenAPI 信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nexus AI 知识库系统")
                        .description("基于 RAG 的企业级知识库系统 API 文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nexus Team")
                                .email("support@nexus.com")));
    }

    /**
     * 分组 A: 系统管理
     * 扫描 com.nexus.system 包下的所有 Controller
     */
    @Bean
    public GroupedOpenApi systemGroupApi() {
        return GroupedOpenApi.builder()
                .group("01-系统管理")
                .packagesToScan("com.nexus.system.controller")
                .build();
    }

    /**
     * 分组 B: 知识库
     * 扫描 com.nexus.knowledge 包下的所有 Controller
     */
    @Bean
    public GroupedOpenApi knowledgeGroupApi() {
        return GroupedOpenApi.builder()
                .group("02-知识库")
                .packagesToScan("com.nexus.knowledge.controller")
                .build();
    }

    /**
     * 分组 C: AI对话
     * 扫描 com.nexus.rag 包下的所有 Controller
     */
    @Bean
    public GroupedOpenApi ragGroupApi() {
        return GroupedOpenApi.builder()
                .group("03-AI对话")
                .packagesToScan("com.nexus.rag.controller")
                .build();
    }
}
