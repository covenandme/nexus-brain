package com.nexus.system.controller.vo;

import com.nexus.system.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果VO
 */
@Schema(description = "登录响应结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultVO {
    
    /**
     * JWT Token
     */
    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserDto userInfo;
}
