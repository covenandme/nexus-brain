package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
