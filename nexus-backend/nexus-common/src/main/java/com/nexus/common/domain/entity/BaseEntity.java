package com.nexus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类
 */
@Data
public abstract class BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 删除标记（0-未删除，删除时设置为当前记录的id值）
     * 配合联合唯一索引使用，避免逻辑删除后的记录与新增记录产生唯一索引冲突
     */
    @TableLogic(value = "0", delval = "id")
    private Long deleted;
}