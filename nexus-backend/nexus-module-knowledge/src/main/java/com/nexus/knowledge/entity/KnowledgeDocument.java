package com.nexus.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文档实体类
 */
@Schema(description = "知识库文档")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_document")
public class KnowledgeDocument extends BaseEntity {
    
    /**
     * 解析状态常量：待解析
     */
    public static final byte STATUS_WAITING = 0;
    
    /**
     * 解析状态常量：解析中
     */
    public static final byte STATUS_PARSING = 1;
    
    /**
     * 解析状态常量：完成
     */
    public static final byte STATUS_COMPLETED = 2;
    
    /**
     * 解析状态常量：失败
     */
    public static final byte STATUS_FAILED = 3;
    
    /**
     * 向量化状态常量：未向量化
     */
    public static final byte VECTOR_STATUS_NOT = 0;
    
    /**
     * 向量化状态常量：已向量化
     */
    public static final byte VECTOR_STATUS_COMPLETED = 1;
    
    /**
     * 所属数据集ID
     */
    @Schema(description = "所属数据集ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("dataset_id")
    private Long datasetId;
    
    /**
     * 所属目录ID (0表示在根目录下)
     */
    @Schema(description = "所属目录ID (0表示在根目录下)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("folder_id")
    private Long folderId;
    
    /**
     * 文件名(包含后缀)
     */
    @Schema(description = "文件名(包含后缀)", example = "示例文档.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("name")
    private String name;
    
    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)", example = "102400", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("size")
    private Long size;
    
    /**
     * 文件后缀
     */
    @Schema(description = "文件后缀", example = "pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("suffix")
    private String suffix;
    
    /**
     * MinIO存储路径
     */
    @Schema(description = "MinIO存储路径", example = "knowledge/2024/01/xxx.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("minio_object_name")
    private String minioObjectName;
    
    /**
     * 文件Hash
     */
    @Schema(description = "文件Hash", example = "abc123...")
    @TableField("content_hash")
    private String contentHash;
    
    /**
     * 解析状态: 0-待解析, 1-解析中, 2-完成, 3-失败
     */
    @Schema(description = "解析状态: 0-待解析, 1-解析中, 2-完成, 3-失败", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("status")
    private Byte status;
    
    /**
     * 向量化状态: 0-未, 1-完
     */
    @Schema(description = "向量化状态: 0-未, 1-完", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("vector_status")
    private Byte vectorStatus;
    
    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    @TableField("parse_error_msg")
    private String parseErrorMsg;
    
    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("create_user")
    private Long createUser;
}
