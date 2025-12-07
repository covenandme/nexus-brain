package com.nexus.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册DTO
 */
@Schema(description = "用户注册请求参数")
@Data
public class UserRegisterDTO {
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "testuser", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6, maxLength = 20)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 6, max = 20, message = "用户名长度必须在6-20个字符之间")
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6, maxLength = 20)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "测试用户", maxLength = 50)
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "test@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;
}