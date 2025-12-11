package com.nexus.rag.service;

import com.nexus.common.dto.rag.ChatRequest;
import com.nexus.common.dto.rag.ChatResponse;

/**
 * RAG 对话服务接口
 */
public interface RagChatService {
    
    /**
     * RAG 对话
     * 
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chat(ChatRequest request);
}
