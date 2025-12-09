package com.nexus.knowledge.service;

import com.nexus.knowledge.entity.KnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档服务接口
 */
public interface DocumentService {
    
    /**
     * 上传文件（支持秒传）
     * 
     * @param file 上传的文件
     * @param datasetId 数据集ID
     * @param folderId 目录ID（0表示根目录）
     * @param currentUserId 当前用户ID
     * @return 文档实体
     */
    KnowledgeDocument uploadFile(MultipartFile file, Long datasetId, Long folderId, Long currentUserId);
}
