package com.nexus.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 登录用户信息
 * 实现Spring Security的UserDetails接口
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（不会序列化到JSON）
     */
    @JsonIgnore
    private String password;
    
    /**
     * 账户是否启用
     */
    private Boolean enabled;
    
    /**
     * 用户权限集合
     */
    private Collection<? extends GrantedAuthority> authorities;
    
    /**
     * 全参构造函数
     */
    public LoginUser(Long userId, String username, String password, Boolean enabled, 
                     Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled != null ? enabled : true;
    }
}
