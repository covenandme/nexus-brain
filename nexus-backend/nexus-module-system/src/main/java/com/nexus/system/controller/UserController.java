package com.nexus.system.controller;

import com.nexus.common.result.PageResult;
import com.nexus.common.result.Result;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户相关的接口，包括注册、查询、删除等操作")
@RestController
@RequestMapping("/system/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "新用户注册，会自动创建用户、分配角色和初始化私有群")
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        boolean result = userService.register(userRegisterDTO);
        return Result.success(result);
    }

    /**
     * 根据ID获取用户信息
     */
    @Operation(summary = "根据ID查询用户", description = "根据用户ID查询用户详细信息")
    @GetMapping("/{id}")
    public Result<UserDto> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return Result.success(userDto);
    }

    /**
     * 查询所有用户（简单列表）
     */
    @Operation(summary = "分页查询用户列表", description = "分页查询所有用户信息")
    @GetMapping
    public Result<PageResult<UserDto>> getAll(
            @Parameter(description = "页码，默认1", example = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小，默认10", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<UserDto> list = userService.getAllUsers(pageNum, pageSize);
        return Result.success(list);
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Boolean> removeById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        boolean removed = userService.removeById(id);
        return Result.success(removed);
    }
}