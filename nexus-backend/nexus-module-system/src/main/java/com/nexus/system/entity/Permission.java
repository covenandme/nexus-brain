package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {
    
    /**
     * 权限名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 权限标识
     */
    @TableField("code")
    private String code;
}
