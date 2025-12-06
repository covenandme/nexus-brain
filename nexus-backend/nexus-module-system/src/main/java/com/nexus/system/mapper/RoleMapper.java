package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
