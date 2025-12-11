package com.nexus.rag.controller;

import com.nexus.common.dto.rag.ChatRequest;
import com.nexus.common.dto.rag.ChatResponse;
import com.nexus.common.result.Result;
import com.nexus.rag.service.RagChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * RAG 对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/rag/chat")
@Tag(name = "RAG对话", description = "RAG智能对话接口")
public class ChatController {
    
    @Autowired
    private RagChatService ragChatService;
    
    /**
     * 发送对话消息
     * 
     * @param request 对话请求
     * @return 对话响应
     */
    @PostMapping("/send")
    @Operation(summary = "发送对话消息", description = "向RAG系统发送问题，获取基于知识库的智能回答")
    public Result<ChatResponse> send(@RequestBody @Parameter(description = "对话请求") ChatRequest request) {
        log.info("收到对话请求: question={}, docId={}", request.getQuestion(), request.getDocId());
        
        ChatResponse response = ragChatService.chat(request);
        
        log.info("对话响应成功: question={}, answerLength={}, snippetsCount={}", 
                request.getQuestion(), 
                response.getAnswer().length(), 
                response.getSourceSnippets().size());
        
        return Result.success(response);
    }
}
