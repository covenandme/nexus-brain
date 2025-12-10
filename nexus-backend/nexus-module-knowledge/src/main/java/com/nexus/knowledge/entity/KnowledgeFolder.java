package com.nexus.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库目录实体类
 */
@Schema(description = "知识库目录")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_folder")
public class KnowledgeFolder extends BaseEntity {
    
    /**
     * 所属数据集ID
     */
    @Schema(description = "所属数据集ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("dataset_id")
    private Long datasetId;
    
    /**
     * 父目录ID (0表示根目录)
     */
    @Schema(description = "父目录ID (0表示根目录)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("parent_id")
    private Long parentId;
    
    /**
     * 目录名称
     */
    @Schema(description = "目录名称", example = "第一章", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("name")
    private String name;
    
    /**
     * 排序号
     */
    @Schema(description = "排序号", example = "0")
    @TableField("sort")
    private Integer sort;
    
    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("create_user")
    private Long createUser;
}
