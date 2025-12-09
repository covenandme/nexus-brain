package com.nexus.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库数据集实体类
 */
@Schema(description = "知识库数据集")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_dataset")
public class KnowledgeDataset extends BaseEntity {
    
    /**
     * 所属团队ID
     */
    @Schema(description = "所属团队ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("team_id")
    private Long teamId;
    
    /**
     * 知识库名称
     */
    @Schema(description = "知识库名称", example = "我的知识库", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("name")
    private String name;
    
    /**
     * 描述
     */
    @Schema(description = "描述", example = "这是一个知识库")
    @TableField("description")
    private String description;
    
    /**
     * 封面/图标URL
     */
    @Schema(description = "封面/图标URL", example = "http://example.com/avatar.png")
    @TableField("avatar")
    private String avatar;
    
    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("create_user")
    private Long createUser;
}
