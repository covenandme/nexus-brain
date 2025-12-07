package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.common.result.PageResult;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param dto 注册DTO
     * @return 是否注册成功
     */
    boolean register(UserRegisterDTO dto);
    UserDto getUserById(Long id);
    PageResult<UserDto> getAllUsers(int pageNum, int pageSize);

}