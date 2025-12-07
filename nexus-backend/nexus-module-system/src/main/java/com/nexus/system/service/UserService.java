package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.common.result.PageResult;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserLoginDTO;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.entity.User;
import com.nexus.system.controller.vo.LoginResultVO;

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
    
    /**
     * 用户登录
     * @param dto 登录DTO
     * @return 登录结果（包含Token和用户信息）
     */
    LoginResultVO login(UserLoginDTO dto);
    
    UserDto getUserById(Long id);
    PageResult<UserDto> getAllUsers(int pageNum, int pageSize);

}