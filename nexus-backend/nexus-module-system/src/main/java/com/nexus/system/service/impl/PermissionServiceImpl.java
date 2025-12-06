package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.system.entity.Permission;
import com.nexus.system.mapper.PermissionMapper;
import com.nexus.system.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
}
