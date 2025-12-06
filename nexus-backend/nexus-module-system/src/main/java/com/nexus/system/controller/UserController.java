package com.nexus.system.controller;

import com.nexus.common.result.PageResult;
import com.nexus.common.result.Result;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/system/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        boolean result = userService.register(userRegisterDTO);
        return Result.success(result);
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return Result.success(userDto);
    }

    /**
     * 查询所有用户（简单列表）
     */
    @GetMapping
    public Result<PageResult<UserDto>> getAll(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<UserDto> list = userService.getAllUsers(pageNum, pageSize);
        return Result.success(list);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> removeById(@PathVariable Long id) {
        boolean removed = userService.removeById(id);
        return Result.success(removed);
    }
}