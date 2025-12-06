package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.ResultCode;
import com.nexus.system.entity.Role;
import com.nexus.system.mapper.RoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 角色服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @Spy
    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setCode("ROLE_USER");
        role.setName("普通用户");
        role.setDescription("普通用户角色");
    }

    @Test
    void testGetByCode_Success() {
        // 模拟：角色存在
        doReturn(role).when(roleService).getOne(any(LambdaQueryWrapper.class));

        // 执行测试
        Role result = roleService.getByCode("ROLE_USER");

        // 验证结果
        assertNotNull(result);
        assertEquals(role.getId(), result.getId());
        assertEquals(role.getCode(), result.getCode());
        assertEquals(role.getName(), result.getName());
        
        // 验证查询条件
        verify(roleService, times(1)).getOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetByCode_NotFound() {
        // 模拟：角色不存在
        doReturn(null).when(roleService).getOne(any(LambdaQueryWrapper.class));

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.getByCode("ROLE_ADMIN");
        });

        assertEquals(ResultCode.NOTFOUND.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("角色不存在"));
    }
}
