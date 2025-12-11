package com.nexus.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 主数据源配置 (MySQL)
 * 作用：显式定义主数据源，防止因为定义了辅助数据源(PG)导致主数据源自动配置失效
 */
@Configuration
public class PrimaryDatabaseConfig {

    /**
     * 1. 读取 YAML 中 spring.datasource 的配置 (MySQL)
     */
    @Bean
    @Primary // 👑 核心注解！告诉 Spring 这是"正宫娘娘"
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 2. 创建 MySQL 数据源
     */
    @Bean
    @Primary // 👑 核心注解！MyBatis 默认会找标有 @Primary 的数据源
    public DataSource dataSource() {
        return primaryDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class) // 明确指定使用 HikariCP
                .build();
    }
}