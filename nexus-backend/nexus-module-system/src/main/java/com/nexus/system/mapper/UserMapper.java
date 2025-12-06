package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}