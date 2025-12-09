package com.nexus.common.security;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 领域用户详情服务接口
 * 扩展Spring Security的UserDetailsService，添加按用户ID加载的方法
 */
public interface DomainUserDetailsService extends UserDetailsService {
    
    /**
     * 根据用户ID加载用户详情
     * @param userId 用户ID
     * @return 登录用户信息
     */
    LoginUser loadUserById(Long userId);
}
