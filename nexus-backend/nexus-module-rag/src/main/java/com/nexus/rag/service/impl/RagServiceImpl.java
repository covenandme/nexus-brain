package com.nexus.rag.service.impl;

import com.nexus.infrastructure.minio.MinioTemplate;
import com.nexus.knowledge.entity.KnowledgeDocument;
import com.nexus.knowledge.mapper.KnowledgeDocumentMapper;
import com.nexus.rag.service.RagService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * RAG 服务实现类
 */
@Slf4j
@Service
public class RagServiceImpl implements RagService {
    
    @Autowired
    private KnowledgeDocumentMapper documentMapper;
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private VectorStore vectorStore;
    
    @Value("${nexus.minio.bucket-name}")
    private String bucketName;
    
    @Override
    public void parseAndStore(Long docId) {
        log.info("开始解析文档: docId={}", docId);
        
        try {
            // 步骤1: 查询文档
            KnowledgeDocument document = documentMapper.selectById(docId);
            if (document == null) {
                log.warn("文档不存在，跳过处理: docId={}", docId);
                return;
            }
            
            // 步骤2: 更新状态为解析中
            document.setStatus(KnowledgeDocument.STATUS_PARSING);
            documentMapper.updateById(document);
            log.info("文档状态已更新为解析中: docId={}", docId);
            
            // 步骤3: 从 MinIO 下载文件
            log.info("开始下载文件: objectName={}", document.getMinioObjectName());
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(document.getMinioObjectName())
                            .build()
            );
            
            // 步骤4: Tika 解析（Extract）
            log.info("开始 Tika 解析文档: docId={}", docId);
            TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(inputStream));
            List<Document> documents = reader.get();
            log.info("Tika 解析完成，提取到 {} 个文档片段", documents.size());
            
            // 如果没有提取到内容，标记为失败
            if (documents.isEmpty()) {
                throw new RuntimeException("文档解析失败：未提取到任何内容");
            }
            
            // 步骤5: 文本切片（Transform/Chunking）
            log.info("开始文本切片: docId={}", docId);
            TokenTextSplitter splitter = new TokenTextSplitter(
                    500,   // defaultChunkSize - 智谱上下文较长，可以设大点
                    300,   // minChunkSizeChars
                    5,     // minChunkLengthToEmbed
                    10000, // maxNumChunks
                    true   // keepSeparator
            );
            List<Document> splitDocs = splitter.apply(documents);
            log.info("文本切片完成，共 {} 个分片", splitDocs.size());
            
            // 步骤6: 元数据增强（关键！）
            log.info("开始元数据增强: docId={}", docId);
            for (Document splitDoc : splitDocs) {
                // 添加 docId 用于后续搜索过滤
                splitDoc.getMetadata().put("docId", String.valueOf(docId));
                splitDoc.getMetadata().put("fileName", document.getName());
                splitDoc.getMetadata().put("datasetId", String.valueOf(document.getDatasetId()));
            }
            log.info("元数据增强完成");
            
            // 步骤7: 向量化与存储（Load）
            log.info("开始向量化并存储: docId={}, 分片数={}", docId, splitDocs.size());
            vectorStore.add(splitDocs);
            log.info("向量化存储完成: docId={}", docId);
            
            // 步骤8: 更新状态为完成
            document.setStatus(KnowledgeDocument.STATUS_COMPLETED);
            document.setVectorStatus(KnowledgeDocument.VECTOR_STATUS_COMPLETED);
            documentMapper.updateById(document);
            
            log.info("文档解析成功: docId={}, status={}, vectorStatus={}", 
                    docId, document.getStatus(), document.getVectorStatus());
            
        } catch (Exception e) {
            log.error("文档解析失败: docId={}", docId, e);
            
            // 更新为失败状态
            try {
                KnowledgeDocument document = documentMapper.selectById(docId);
                if (document != null) {
                    document.setStatus(KnowledgeDocument.STATUS_FAILED);
                    document.setParseErrorMsg(e.getMessage());
                    documentMapper.updateById(document);
                    log.info("文档状态已更新为失败: docId={}", docId);
                }
            } catch (Exception updateException) {
                log.error("更新文档失败状态时出错: docId={}", docId, updateException);
            }
            
            // 重新抛出异常，让监听器知道处理失败
            throw new RuntimeException("文档解析失败: " + e.getMessage(), e);
        }
    }
}
