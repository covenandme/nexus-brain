package com.nexus.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.common.result.PageResult;
import com.nexus.common.result.ResultCode;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterDTO registerDTO;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("测试用户");
        registerDTO.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setNickname("测试用户");
        userDto.setEmail("test@example.com");
    }

//    /**
//     * 创建 PageResult 实例（使用反射，因为构造方法是私有的）
//     */
//    @SuppressWarnings("unchecked")
//    private <T> PageResult<T> createPageResult() {
//        try {
//            return (PageResult<T>) PageResult.class.getDeclaredConstructor().newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create PageResult instance", e);
//        }
//    }

    @Test
    void testRegister_Success() throws Exception {
        // 模拟：注册成功
        when(userService.register(any(UserRegisterDTO.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/system/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 验证方法调用
        verify(userService, times(1)).register(any(UserRegisterDTO.class));
    }

    @Test
    void testRegister_ValidationFailed() throws Exception {
        // 测试验证失败（用户名为空）
        registerDTO.setUsername("");

        mockMvc.perform(post("/system/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // 模拟：用户存在
        when(userService.getUserById(1L)).thenReturn(userDto);

        // 执行测试
        mockMvc.perform(get("/system/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        // 验证方法调用
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        // 模拟：分页查询
        PageResult<UserDto> pageResult = PageResult.of(
                Collections.emptyList(), // records
                1L,                      // total
                1L,                      // current
                10L                      // size
        );

        when(userService.getAllUsers(1, 10)).thenReturn(pageResult);

        // 执行测试
        mockMvc.perform(get("/system/users")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.current").value(1L))
                .andExpect(jsonPath("$.data.size").value(10L))
                .andExpect(jsonPath("$.data.total").value(1L));

        // 验证方法调用
        verify(userService, times(1)).getAllUsers(1, 10);
    }

    @Test
    void testGetAllUsers_DefaultParams() throws Exception {
        // 测试默认参数
        PageResult<UserDto> pageResult = PageResult.of(
                Collections.emptyList(), // records
                1L,                      // total
                1L,                      // current
                10L                      // size
        );
        when(userService.getAllUsers(1, 10)).thenReturn(pageResult);

        mockMvc.perform(get("/system/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers(1, 10);
    }

    @Test
    void testRemoveById_Success() throws Exception {
        // 模拟：删除成功
        when(userService.removeById(1L)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/system/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 验证方法调用
        verify(userService, times(1)).removeById(1L);
    }
}
