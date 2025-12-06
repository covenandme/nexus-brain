package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.system.entity.RolePermission;
import com.nexus.system.mapper.RolePermissionMapper;
import com.nexus.system.service.RolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色-权限关联服务实现类
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
