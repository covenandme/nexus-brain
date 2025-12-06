package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-团队关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_team")
public class TeamMember extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 团队ID
     */
    @TableField("team_id")
    private Long teamId;
    
    /**
     * 团队角色ID: 1-OWNER, 2-MEMBER等
     */
    @TableField("role_id")
    private Integer roleId;
}
