package com.nexus.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户信息DTO")
@Data
public class UserDto {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "testuser")
    private String username;
    
    @Schema(description = "昵称", example = "测试用户")
    private String nickname;
    
    @Schema(description = "邮箱", example = "test@example.com")
    private String email;
}
