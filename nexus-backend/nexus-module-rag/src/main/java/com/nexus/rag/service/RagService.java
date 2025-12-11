package com.nexus.rag.service;

/**
 * RAG 服务接口
 */
public interface RagService {
    
    /**
     * 解析文档并存储向量
     * 
     * @param docId 文档ID
     */
    void parseAndStore(Long docId);
}
