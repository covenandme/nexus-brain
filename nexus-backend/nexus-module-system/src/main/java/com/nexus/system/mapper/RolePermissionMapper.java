package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-权限关联Mapper接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
