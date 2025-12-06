package com.nexus.system.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
}
