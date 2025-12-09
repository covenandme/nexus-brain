package com.nexus.infrastructure.minio;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 操作模板类
 * 封装 MinIO 的核心操作方法
 */
@Slf4j
@Component
public class MinioTemplate {
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioProperties minioProperties;
    
    /**
     * 上传文件流
     *
     * @param inputStream 文件输入流
     * @param objectName  对象名称（文件路径）
     * @param contentType 文件类型
     * @return 文件访问路径
     */
    public String upload(InputStream inputStream, String objectName, String contentType) {
        try {
            log.info("开始上传文件到 MinIO: bucket={}, objectName={}, contentType={}", 
                    minioProperties.getBucketName(), objectName, contentType);
            
            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );
            
            log.info("文件上传成功: {}", objectName);
            return objectName;
            
        } catch (Exception e) {
            log.error("文件上传失败: objectName={}, error={}", objectName, e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("关闭文件流失败", e);
            }
        }
    }
    
    /**
     * 获取预览 URL（1小时有效期）
     * 设置 Content-Disposition 为 inline，浏览器将直接预览而不是下载
     *
     * @param objectName       对象名称（文件路径）
     * @param originalFileName 原始文件名
     * @return 预签名 URL
     */
    public String getPreviewUrl(String objectName, String originalFileName) {
        try {
            log.info("生成预览 URL: bucket={}, objectName={}, originalFileName={}", 
                    minioProperties.getBucketName(), objectName, originalFileName);
            
            // 设置响应头，使用 inline 让浏览器预览
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Disposition", "inline; filename=\"" + originalFileName + "\"");
            
            // 生成预签名 URL，有效期 1 小时
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .expiry(1, TimeUnit.HOURS)
                            .extraHeaders(headers)
                            .build()
            );
            
            log.info("预览 URL 生成成功: {}", url);
            return url;
            
        } catch (Exception e) {
            log.error("生成预览 URL 失败: objectName={}, error={}", objectName, e.getMessage(), e);
            throw new RuntimeException("生成预览 URL 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除文件
     *
     * @param objectName 对象名称（文件路径）
     */
    public void remove(String objectName) {
        try {
            log.info("开始删除文件: bucket={}, objectName={}", 
                    minioProperties.getBucketName(), objectName);
            
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
            
            log.info("文件删除成功: {}", objectName);
            
        } catch (Exception e) {
            log.error("文件删除失败: objectName={}, error={}", objectName, e.getMessage(), e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
}
