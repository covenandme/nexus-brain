package com.nexus.rag.listener;

import com.nexus.common.dto.mq.DocParseMsg;
import com.nexus.infrastructure.config.MqConfig;
import com.nexus.knowledge.entity.KnowledgeDocument;
import com.nexus.knowledge.mapper.KnowledgeDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文档解析监听器
 */
@Slf4j
@Component
public class DocParseListener {
    
    @Autowired
    private KnowledgeDocumentMapper documentMapper;
    
    /**
     * 监听文档解析队列
     * 
     * @param msg 文档解析消息
     */
    @RabbitListener(queues = MqConfig.QUEUE_DOC_PARSE)
    public void handle(DocParseMsg msg) {
        log.info("收到解析任务: {}", msg);
        
        Long docId = msg.getDocId();
        
        try {
            // Step 1: 更新数据库，将状态改为解析中
            KnowledgeDocument document = documentMapper.selectById(docId);
            if (document == null) {
                log.error("文档不存在: docId={}", docId);
                return;
            }
            
            document.setStatus(KnowledgeDocument.STATUS_PARSING);
            documentMapper.updateById(document);
            log.info("文档状态已更新为解析中: docId={}", docId);
            
            // Step 2: 模拟 AI 解析耗时
            Thread.sleep(3000);
            log.info("AI 解析完成（模拟）: docId={}", docId);
            
            // Step 3: 更新数据库，将状态改为成功，已向量化
            document.setStatus(KnowledgeDocument.STATUS_COMPLETED);
            document.setVectorStatus(KnowledgeDocument.VECTOR_STATUS_COMPLETED);
            documentMapper.updateById(document);
            
            log.info("解析完成: docId={}, status={}, vectorStatus={}", 
                    docId, document.getStatus(), document.getVectorStatus());
            
        } catch (InterruptedException e) {
            log.error("解析任务被中断: docId={}", docId, e);
            Thread.currentThread().interrupt();
            
            // 更新为失败状态
            updateToFailedStatus(docId, "解析任务被中断");
            
        } catch (Exception e) {
            log.error("解析任务失败: docId={}", docId, e);
            
            // 更新为失败状态
            updateToFailedStatus(docId, e.getMessage());
        }
    }
    
    /**
     * 更新文档为失败状态
     * 
     * @param docId 文档ID
     * @param errorMsg 错误信息
     */
    private void updateToFailedStatus(Long docId, String errorMsg) {
        try {
            KnowledgeDocument document = documentMapper.selectById(docId);
            if (document != null) {
                document.setStatus(KnowledgeDocument.STATUS_FAILED);
                document.setParseErrorMsg(errorMsg);
                documentMapper.updateById(document);
                log.info("文档状态已更新为失败: docId={}, errorMsg={}", docId, errorMsg);
            }
        } catch (Exception e) {
            log.error("更新文档失败状态时出错: docId={}", docId, e);
        }
    }
}
