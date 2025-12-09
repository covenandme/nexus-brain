package com.nexus.infrastructure.config;

import com.nexus.infrastructure.minio.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 */
@Slf4j
@Configuration
public class MinioConfig implements InitializingBean {
    
    @Autowired
    private MinioProperties minioProperties;
    
    private MinioClient minioClient;
    
    /**
     * 创建 MinioClient Bean
     */
    @Bean
    public MinioClient minioClient() {
        log.info("初始化 MinioClient, endpoint: {}", minioProperties.getEndpoint());
        minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        return minioClient;
    }
    
    /**
     * 初始化完成后自动检查并创建存储桶
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String bucketName = minioProperties.getBucketName();
        log.info("检查存储桶是否存在: {}", bucketName);
        
        try {
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            
            if (!bucketExists) {
                // 创建存储桶
                log.info("存储桶不存在，正在创建: {}", bucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("存储桶创建成功: {}", bucketName);
            } else {
                log.info("存储桶已存在: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("初始化 MinIO 存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("初始化 MinIO 存储桶失败", e);
        }
    }
}
