package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
