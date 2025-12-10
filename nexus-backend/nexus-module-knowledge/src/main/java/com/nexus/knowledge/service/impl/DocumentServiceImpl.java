package com.nexus.knowledge.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.dto.mq.DocParseMsg;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.ResultCode;
import com.nexus.infrastructure.config.MqConfig;
import com.nexus.infrastructure.minio.MinioTemplate;
import com.nexus.knowledge.entity.KnowledgeDataset;
import com.nexus.knowledge.entity.KnowledgeDocument;
import com.nexus.knowledge.mapper.KnowledgeDatasetMapper;
import com.nexus.knowledge.mapper.KnowledgeDocumentMapper;
import com.nexus.knowledge.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {
    
    @Autowired
    private KnowledgeDocumentMapper documentMapper;
    
    @Autowired
    private KnowledgeDatasetMapper datasetMapper;
    
    @Autowired
    private MinioTemplate minioTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocument uploadFile(MultipartFile file, Long datasetId, Long folderId, Long currentUserId) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "文件不能为空");
        }
        if (datasetId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "数据集ID不能为空");
        }
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 默认folderId为0（根目录）
        if (folderId == null) {
            folderId = 0L;
        }
        
        try {
            // 步骤1: 检查数据集是否存在
            KnowledgeDataset dataset = datasetMapper.selectById(datasetId);
            if (dataset == null) {
                throw new BusinessException(ResultCode.NOTFOUND, "数据集不存在");
            }
            
            // 步骤2: 计算文件MD5
            String contentHash;
            try (InputStream inputStream = file.getInputStream()) {
                contentHash = SecureUtil.md5(inputStream);
                log.info("文件MD5计算完成: {}", contentHash);
            } catch (Exception e) {
                log.error("计算文件MD5失败", e);
                throw new BusinessException(ResultCode.FAILED, "计算文件MD5失败");
            }
            
            // 步骤3: 秒传逻辑 - 查询是否存在相同contentHash且vectorStatus=1的记录
            LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(KnowledgeDocument::getContentHash, contentHash)
                   .eq(KnowledgeDocument::getVectorStatus, KnowledgeDocument.VECTOR_STATUS_COMPLETED)
                   .last("LIMIT 1");
            KnowledgeDocument existingDoc = documentMapper.selectOne(wrapper);
            
            String minioObjectName;
            boolean isSecondUpload = false;
            
            if (existingDoc != null) {
                // 秒传：复用已有文件的MinIO路径
                minioObjectName = existingDoc.getMinioObjectName();
                isSecondUpload = true;
                log.info("秒传：复用已存在的文件, contentHash={}, minioObjectName={}", contentHash, minioObjectName);
            } else {
                // 步骤4: 非秒传 - 上传到MinIO
                // 生成文件路径：team/{teamId}/dataset/{datasetId}/{yyyyMM}/{uuid}.{ext}
                String originalFilename = file.getOriginalFilename();
                String suffix = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                }
                
                String yearMonth = LocalDateTime.now().format(MONTH_FORMATTER);
                String uuid = IdUtil.simpleUUID();
                minioObjectName = String.format("team/%d/dataset/%d/%s/%s.%s", 
                        dataset.getTeamId(), datasetId, yearMonth, uuid, suffix);
                
                log.info("开始上传文件到MinIO: {}", minioObjectName);
                
                try (InputStream inputStream = file.getInputStream()) {
                    minioTemplate.upload(inputStream, minioObjectName, file.getContentType());
                } catch (Exception e) {
                    log.error("上传文件到MinIO失败", e);
                    throw new BusinessException(ResultCode.FAILED, "文件上传失败");
                }
            }
            
            // 步骤5: 保存数据库记录
            KnowledgeDocument document = new KnowledgeDocument();
            document.setDatasetId(datasetId);
            document.setFolderId(folderId);
            document.setName(file.getOriginalFilename());
            document.setSize(file.getSize());
            
            // 提取文件后缀
            String suffix = "";
            if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            }
            document.setSuffix(suffix);
            
            document.setMinioObjectName(minioObjectName);
            document.setContentHash(contentHash);
            
            // 秒传直接标记为解析成功，否则为待解析
            if (isSecondUpload) {
                document.setStatus(KnowledgeDocument.STATUS_COMPLETED);
                document.setVectorStatus(KnowledgeDocument.VECTOR_STATUS_COMPLETED);
                log.info("秒传文件，直接标记为解析成功和已向量化");
            } else {
                document.setStatus(KnowledgeDocument.STATUS_WAITING);
                document.setVectorStatus(KnowledgeDocument.VECTOR_STATUS_NOT);
            }
            
            document.setCreateUser(currentUserId);
            
            // 尝试插入数据库
            try {
                documentMapper.insert(document);
                log.info("文档记录保存成功: id={}, name={}", document.getId(), document.getName());
                
                // 非秒传的文件需要发送MQ消息进行解析
                if (!isSecondUpload) {
                    sendParseMessage(document.getId());
                }
                
                return document;
            } catch (Exception e) {
                // 如果数据库插入失败且非秒传，需要删除已上传的MinIO文件
                if (!isSecondUpload) {
                    log.error("数据库插入失败，回滚删除MinIO文件: {}", minioObjectName);
                    try {
                        minioTemplate.remove(minioObjectName);
                        log.info("MinIO文件删除成功: {}", minioObjectName);
                    } catch (Exception removeException) {
                        log.error("删除MinIO文件失败: {}", minioObjectName, removeException);
                    }
                }
                
                // 检查是否是唯一约束冲突
                if (e.getMessage() != null && e.getMessage().contains("uk_folder_name_deleted")) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "该目录下已存在同名文件");
                }
                
                log.error("保存文档记录失败", e);
                throw new BusinessException(ResultCode.FAILED, "保存文档记录失败: " + e.getMessage());
            }
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new BusinessException(ResultCode.FAILED, "上传文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送文档解析消息到 RabbitMQ
     * 
     * @param docId 文档ID
     */
    private void sendParseMessage(Long docId) {
        try {
            DocParseMsg msg = new DocParseMsg();
            msg.setDocId(docId);
            msg.setTimestamp(System.currentTimeMillis());
            
            rabbitTemplate.convertAndSend(
                    MqConfig.EXCHANGE_KNOWLEDGE,
                    MqConfig.ROUTING_KEY_PARSE,
                    msg
            );
            
            log.info("文件上传成功，已发送解析消息: {}", msg);
        } catch (Exception e) {
            // 消息发送失败只打印错误日志，不回滚数据库事务（保持上传接口高可用）
            log.error("发送解析消息失败: docId={}", docId, e);
        }
    }
}
