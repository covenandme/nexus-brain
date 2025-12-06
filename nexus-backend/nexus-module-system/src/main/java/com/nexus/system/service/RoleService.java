package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.system.entity.Role;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 根据角色编码查询角色
     * @param code 角色编码
     * @return 角色实体
     */
    Role getByCode(String code);
}
