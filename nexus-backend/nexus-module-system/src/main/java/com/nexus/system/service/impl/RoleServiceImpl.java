package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.ResultCode;
import com.nexus.system.entity.Role;
import com.nexus.system.mapper.RoleMapper;
import com.nexus.system.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    @Override
    public Role getByCode(String code) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, code);
        wrapper.eq(Role::getDeleted, 0);
        Role role = this.getOne(wrapper);
        if (role == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "角色不存在: " + code);
        }
        return role;
    }
}
