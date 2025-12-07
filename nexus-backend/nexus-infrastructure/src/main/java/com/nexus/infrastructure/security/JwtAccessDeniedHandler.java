package com.nexus.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.common.result.Result;
import com.nexus.common.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT访问拒绝处理器
 * 处理403禁止访问错误
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, 
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 设置响应状态码和内容类型
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        
        // 构建返回结果
        Result<Object> result = Result.error(ResultCode.FORBIDDEN);
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}