package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-权限关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {
    
    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;
    
    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Long permissionId;
}
