package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 团队实体类
 */
@Schema(description = "团队信息")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_team")
public class Team extends BaseEntity {
    
    /**
     * 团队名称
     */
    @Schema(description = "团队名称", example = "测试团队", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("name")
    private String name;
    
    /**
     * 团队类型: 1-私有群, 2-公开群
     */
    @Schema(description = "团队类型: 1-私有群, 2-公开群", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("type")
    private Integer type;
    
    /**
     * 团队所有者ID
     */
    @Schema(description = "团队所有者ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("owner_id")
    private Long ownerId;
    
    /**
     * 存储配额（单位：MB）
     */
    @Schema(description = "存储配额（单位：MB）", example = "1024")
    @TableField("storage_quota")
    private Long storageQuota;
}
