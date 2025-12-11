package com.nexus.common.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * RAG 对话响应
 */
@Data
@Schema(description = "RAG对话响应")
public class ChatResponse {
    
    /**
     * AI 的回答
     */
    @Schema(description = "AI的回答")
    private String answer;
    
    /**
     * 引用的原文片段（用于展示来源）
     */
    @Schema(description = "引用的原文片段，展示答案来源")
    private List<String> sourceSnippets;
}
