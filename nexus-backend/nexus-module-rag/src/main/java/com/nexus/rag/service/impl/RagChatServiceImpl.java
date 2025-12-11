package com.nexus.rag.service.impl;

import com.nexus.common.dto.rag.ChatRequest;
import com.nexus.common.dto.rag.ChatResponse;
import com.nexus.rag.service.RagChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG 对话服务实现类
 */
@Slf4j
@Service
public class RagChatServiceImpl implements RagChatService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatModel chatModel;

    /**
     * RAG 提示词模板
     */
    private static final String PROMPT_TEMPLATE = """
            你是一个智能助手。请根据以下提供的上下文信息回答用户的问题。
            如果上下文中没有答案，请诚实地说不知道，不要编造。
            
            上下文信息：
            {context}
            
            用户问题：
            {question}
            """;

    @Override
    public ChatResponse chat(ChatRequest request) {
        log.info("开始处理RAG对话: question={}, docId={}", request.getQuestion(), request.getDocId());

        // 步骤1: 向量检索 - 查找最相似的文档片段
        List<Document> similarDocs = retrieveSimilarDocuments(request);

        if (similarDocs.isEmpty()) {
            log.warn("未找到相关文档片段: question={}", request.getQuestion());
            return buildEmptyResponse();
        }

        log.info("检索到 {} 个相关文档片段", similarDocs.size());

        // 步骤2: 构建上下文 - 将检索到的片段拼接成字符串
        String context = buildContext(similarDocs);

        // 步骤3: 构建提示词
        Prompt prompt = buildPrompt(context, request.getQuestion());

        // 步骤4: 调用 AI 生成回答
        String answer = chatModel.call(prompt).getResult().getOutput().getContent();

        log.info("AI回答生成成功: question={}, answerLength={}", request.getQuestion(), answer.length());

        // 步骤5: 封装响应，包含答案和引用片段
        return buildResponse(answer, similarDocs);
    }

    /**
     * 向量检索 - 查找最相似的文档片段
     */
    private List<Document> retrieveSimilarDocuments(ChatRequest request) {
        // 1. 直接创建 SearchRequest 对象 (不再是用 Builder)
        SearchRequest searchRequest = SearchRequest.query(request.getQuestion())
                .withTopK(3);  // 取前 3 个

        // 2. 如果指定了 docId，添加过滤条件
        if (request.getDocId() != null) {
            log.info("添加文档过滤条件: docId={}", request.getDocId());

            // 构建过滤表达式
            Filter.Expression filterExpression = new FilterExpressionBuilder()
                    .eq("docId", String.valueOf(request.getDocId()) ) // 这里 Spring AI 会自动处理类型，或者传 String 也可以
                    .build();

            // ⚠️ 注意：SearchRequest 是不可变的，必须把返回值重新赋给 searchRequest
            searchRequest = searchRequest.withFilterExpression(filterExpression);
        }

        // 3. 执行检索 (不需要 .build())
        List<Document> results = vectorStore.similaritySearch(searchRequest);

        log.info("向量检索完成: query={}, docId={}, resultsCount={}",
                request.getQuestion(), request.getDocId(), results.size());

        return results;
    }

    /**
     * 构建上下文 - 将检索到的文档片段拼接成字符串
     */
    private String buildContext(List<Document> documents) {
        return documents.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * 构建提示词
     */
    private Prompt buildPrompt(String context, String question) {
        PromptTemplate promptTemplate = new PromptTemplate(PROMPT_TEMPLATE);

        Map<String, Object> params = new HashMap<>();
        params.put("context", context);
        params.put("question", question);

        return promptTemplate.create(params);
    }

    /**
     * 构建响应
     */
    private ChatResponse buildResponse(String answer, List<Document> sourceDocs) {
        ChatResponse response = new ChatResponse();
        response.setAnswer(answer);

        // 提取引用片段（截取前 200 个字符作为摘要）
        List<String> snippets = sourceDocs.stream()
                .map(doc -> {
                    String content = doc.getContent();
                    if (content.length() > 200) {
                        return content.substring(0, 200) + "...";
                    }
                    return content;
                })
                .collect(Collectors.toList());

        response.setSourceSnippets(snippets);

        return response;
    }

    /**
     * 构建空响应（未找到相关文档）
     */
    private ChatResponse buildEmptyResponse() {
        ChatResponse response = new ChatResponse();
        response.setAnswer("抱歉，我在知识库中没有找到与您问题相关的信息。");
        response.setSourceSnippets(List.of());
        return response;
    }
}
