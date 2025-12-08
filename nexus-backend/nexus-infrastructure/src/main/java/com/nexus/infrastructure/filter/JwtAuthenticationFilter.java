package com.nexus.infrastructure.filter;

import com.nexus.common.security.DomainUserDetailsService;
import com.nexus.common.security.LoginUser;
import com.nexus.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 拦截请求头中的Authorization，验证Token并设置SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final DomainUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取Authorization请求头
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // 如果请求头为空或不是Bearer开头，直接放行（由SecurityConfig处理）
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取Token
        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 验证Token
            if (JwtUtil.validateToken(token)) {
                // 解析Token获取userId
                Long userId = JwtUtil.getUserIdFromToken(token);

                // 从数据库加载用户详情（包含角色信息）
                LoginUser loginUser = userDetailsService.loadUserById(userId);

                // 创建认证对象，使用userId作为principal
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                loginUser,  // 使用userId作为principal
                                null,
                                loginUser.getAuthorities()  // 使用从数据库加载的真实角色
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token验证失败，清除SecurityContext
            SecurityContextHolder.clearContext();
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
