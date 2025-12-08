package com.nexus.system.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.security.DomainUserDetailsService;
import com.nexus.common.security.LoginUser;
import com.nexus.system.entity.Role;
import com.nexus.system.entity.User;
import com.nexus.system.entity.UserRole;
import com.nexus.system.mapper.RoleMapper;
import com.nexus.system.mapper.UserMapper;
import com.nexus.system.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户详情服务实现
 * 实现DomainUserDetailsService接口，从数据库加载用户信息和角色
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements DomainUserDetailsService {
    
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    
    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 查询用户的角色
        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(user.getId());
        
        // 构建并返回LoginUser对象
        return new LoginUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                true, // enabled - 可以根据user实体的status字段来设置
                authorities
        );
    }
    
    @Override
    public LoginUser loadUserById(Long userId) throws UsernameNotFoundException {
        // 查询用户
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在，ID: " + userId);
        }
        
        // 查询用户的角色
        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(userId);
        
        // 构建并返回LoginUser对象
        return new LoginUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                true, // enabled - 可以根据user实体的status字段来设置
                authorities
        );
    }
    
    /**
     * 加载用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    private Collection<? extends GrantedAuthority> loadUserAuthorities(Long userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 查询用户角色关联
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        
        // 查询角色信息并构建权限
        for (UserRole userRole : userRoles) {
            Role role = roleMapper.selectById(userRole.getRoleId());
            if (role != null && role.getCode() != null) {
                // Spring Security角色需要ROLE_前缀
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            }
        }
        
        // 如果用户没有任何角色，添加默认角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
}
