package com.nexus.infrastructure.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 向量数据库专用配置
 * 作用：将 VectorStore 强制指向 PostgreSQL 数据源，而不是主 MySQL 数据源
 */
@Configuration
public class VectorStoreConfig {

    /**
     * 1. 读取 YAML 中 spring.datasource-vector 的配置
     */
    @Bean
    @ConfigurationProperties("spring.datasource-vector")
    public DataSourceProperties vectorDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 2. 创建 PG 数据源
     */
    @Bean
    public DataSource vectorDataSource() {
        return vectorDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * 3. 创建 PG 专用的 JdbcTemplate
     */
    @Bean
    public JdbcTemplate vectorJdbcTemplate(@Qualifier("vectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public VectorStore vectorStore(JdbcTemplate vectorJdbcTemplate,
                                   EmbeddingModel embeddingModel,
                                   // 👇 必须手动注入配置，因为自动配置被我们要么覆盖要么禁用了
                                   @Value("${spring.ai.vectorstore.pgvector.dimensions:1024}") int dimensions,
                                   @Value("${spring.ai.vectorstore.pgvector.initialize-schema:true}") boolean initializeSchema) {

        // 显式调用构造函数，传入 1024
        return new PgVectorStore(
                vectorJdbcTemplate,                                // 1. JdbcTemplate
                embeddingModel,                                    // 2. EmbeddingModel
                dimensions,                                        // 3. 维度 (1024)
                PgVectorStore.PgDistanceType.COSINE_DISTANCE,      // 4. 距离计算方式 (智谱/OpenAI 推荐余弦距离)
                false,                                             // 5. 是否移除已存在的表 (生产环境千万选 false!)
                PgVectorStore.PgIndexType.HNSW,                    // 6. 索引类型 (HNSW)
                initializeSchema                                   // 7. 是否初始化 Schema (建表)
        );
    }
}