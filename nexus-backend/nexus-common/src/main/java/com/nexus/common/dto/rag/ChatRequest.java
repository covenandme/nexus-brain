package com.nexus.common.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * RAG 对话请求
 */
@Data
@Schema(description = "RAG对话请求")
public class ChatRequest {
    
    /**
     * 文档ID（可选，如果用户想针对特定文档提问）
     */
    @Schema(description = "文档ID，可选，指定后只在该文档范围内检索")
    private Long docId;
    
    /**
     * 用户的问题（必填）
     */
    @Schema(description = "用户的问题", required = true)
    private String question;
    
    /**
     * 对话历史（可选，保留，暂不实现，用于多轮对话上下文）
     */
    @Schema(description = "对话历史，保留字段，用于未来多轮对话")
    private List<String> history;
}
