package com.nexus.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {
    
    /**
     * 角色名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 角色编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
}
