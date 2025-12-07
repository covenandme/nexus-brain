package com.nexus.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.common.result.Result;
import com.nexus.common.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * 处理401未授权错误
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        // 设置响应状态码和内容类型
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        // 构建返回结果
        Result<Object> result = Result.error(ResultCode.UNAUTHORIZED);
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}