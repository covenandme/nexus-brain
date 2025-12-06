package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.system.entity.UserRole;
import com.nexus.system.mapper.UserRoleMapper;
import com.nexus.system.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户-角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
