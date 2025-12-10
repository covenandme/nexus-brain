package com.nexus.knowledge.controller;

import com.nexus.common.result.Result;
import com.nexus.common.security.LoginUser;
import com.nexus.infrastructure.security.CurrentUser;
import com.nexus.knowledge.entity.KnowledgeDocument;
import com.nexus.knowledge.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档控制器
 */
@Tag(name = "文档管理", description = "知识库文档相关的接口，包括文件上传、查询等操作")
@RestController
@RequestMapping("/knowledge/document")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * 上传文件（支持秒传）
     */
    @Operation(summary = "上传文件", description = "上传文件到知识库，支持秒传功能。如果文件已存在且已向量化，将直接复用，无需重新上传")
    @PostMapping("/upload")
    public Result<KnowledgeDocument> uploadFile(
            @CurrentUser LoginUser loginUser,
            @Parameter(description = "上传的文件", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "数据集ID", required = true)
            @RequestParam Long datasetId,
            @Parameter(description = "目录ID（0表示根目录）", example = "0")
            @RequestParam(defaultValue = "0") Long folderId) {
        
        KnowledgeDocument document = documentService.uploadFile(
                file, 
                datasetId, 
                folderId, 
                loginUser.getUserId()
        );
        
        return Result.success(document);
    }
}
