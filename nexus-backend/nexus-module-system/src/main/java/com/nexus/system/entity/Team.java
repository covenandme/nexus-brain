package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 团队实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_team")
public class Team extends BaseEntity {
    
    /**
     * 团队名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 团队类型: 1-私有群, 2-公开群
     */
    @TableField("type")
    private Integer type;
    
    /**
     * 团队所有者ID
     */
    @TableField("owner_id")
    private Long ownerId;
    
    /**
     * 存储配额（单位：MB）
     */
    @TableField("storage_quota")
    private Long storageQuota;
}
