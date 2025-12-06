package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.ResultCode;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.entity.*;
import com.nexus.system.mapper.UserMapper;
import com.nexus.system.service.*;
import com.nexus.system.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private TeamService teamService;

    @Mock
    private TeamMemberService teamMemberService;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterDTO registerDTO;
    private User savedUser;
    private Role role;
    private Team team;
    private UserRole userRole;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("测试用户");
        registerDTO.setEmail("test@example.com");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encoded_password");
        savedUser.setNickname("测试用户");
        savedUser.setEmail("test@example.com");

        role = new Role();
        role.setId(1L);
        role.setCode("ROLE_USER");
        role.setName("普通用户");

        team = new Team();
        team.setId(1L);
        team.setName("testuser的私有群");
        team.setType(1);
        team.setOwnerId(1L);
        team.setStorageQuota(1024L);

        userRole = new UserRole();
        userRole.setUserId(1L);
        userRole.setRoleId(1L);

        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setTeamId(1L);
        teamMember.setRoleId(1);
    }

    @Test
    void testRegister_Success() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：查询角色
        when(roleService.getByCode("ROLE_USER")).thenReturn(role);
        // 模拟：保存用户角色关联
        when(userRoleService.save(any(UserRole.class))).thenReturn(true);
        // 模拟：保存团队
        doAnswer(invocation -> {
            Team t = invocation.getArgument(0);
            t.setId(1L);
            return true;
        }).when(teamService).save(any(Team.class));
        // 模拟：保存团队成员
        when(teamMemberService.save(any(TeamMember.class))).thenReturn(true);

        // 执行测试
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            boolean result = userService.register(registerDTO);
            
            // 验证结果
            assertTrue(result);
            
            // 验证方法调用
            verify(userService, times(2)).getOne(any(LambdaQueryWrapper.class)); // 用户名和邮箱校验
            verify(userService, times(1)).save(any(User.class));
            verify(roleService, times(1)).getByCode("ROLE_USER");
            verify(userRoleService, times(1)).save(any(UserRole.class));
            verify(teamService, times(1)).save(any(Team.class));
            verify(teamMemberService, times(1)).save(any(TeamMember.class));
        }
    }

    @Test
    void testRegister_UsernameExists() {
        // 模拟：用户名已存在
        doReturn(savedUser).when(userService).getOne(any(LambdaQueryWrapper.class));

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });

        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());
        
        // 验证后续方法未被调用
        verify(userService, never()).save(any(User.class));
        verify(roleService, never()).getByCode(anyString());
    }

    @Test
    void testRegister_EmailExists() {
        // 模拟：用户名不存在，但邮箱已存在
        doReturn(null)  // 用户名查询返回null
                .doReturn(savedUser)  // 邮箱查询返回已存在用户
                .when(userService).getOne(any(LambdaQueryWrapper.class));

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });

        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), exception.getCode());
        assertEquals("邮箱已被注册", exception.getMessage());
        
        // 验证后续方法未被调用
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testRegister_NoEmail() {
        // 测试没有邮箱的情况
        registerDTO.setEmail(null);
        
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：查询角色
        when(roleService.getByCode("ROLE_USER")).thenReturn(role);
        // 模拟：保存用户角色关联
        when(userRoleService.save(any(UserRole.class))).thenReturn(true);
        // 模拟：保存团队
        doAnswer(invocation -> {
            Team t = invocation.getArgument(0);
            t.setId(1L);
            return true;
        }).when(teamService).save(any(Team.class));
        // 模拟：保存团队成员
        when(teamMemberService.save(any(TeamMember.class))).thenReturn(true);

        // 执行测试
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            boolean result = userService.register(registerDTO);
            
            assertTrue(result);
            // 验证只查询了一次（用户名），没有查询邮箱
            verify(userService, times(1)).getOne(any(LambdaQueryWrapper.class));
        }
    }

    @Test
    void testRegister_RoleNotFound() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：角色不存在
        when(roleService.getByCode("ROLE_USER")).thenThrow(
                new BusinessException(ResultCode.NOTFOUND, "角色不存在: ROLE_USER")
        );

        // 执行测试并验证异常
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.register(registerDTO);
            });

            assertEquals(ResultCode.NOTFOUND.getCode(), exception.getCode());
            assertTrue(exception.getMessage().contains("角色不存在"));
        }
    }

    @Test
    void testRegister_UserSaveFailed() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户失败
        doReturn(false).when(userService).save(any(User.class));

        // 执行测试并验证异常
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.register(registerDTO);
            });

            assertEquals(ResultCode.FAILED.getCode(), exception.getCode());
            assertEquals("用户注册失败", exception.getMessage());
        }
    }

    @Test
    void testRegister_RoleAssignFailed() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：查询角色
        when(roleService.getByCode("ROLE_USER")).thenReturn(role);
        // 模拟：保存用户角色关联失败
        when(userRoleService.save(any(UserRole.class))).thenReturn(false);

        // 执行测试并验证异常
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.register(registerDTO);
            });

            assertEquals(ResultCode.FAILED.getCode(), exception.getCode());
            assertEquals("角色分配失败", exception.getMessage());
        }
    }

    @Test
    void testRegister_TeamSaveFailed() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：查询角色
        when(roleService.getByCode("ROLE_USER")).thenReturn(role);
        // 模拟：保存用户角色关联
        when(userRoleService.save(any(UserRole.class))).thenReturn(true);
        // 模拟：保存团队失败
        when(teamService.save(any(Team.class))).thenReturn(false);

        // 执行测试并验证异常
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.register(registerDTO);
            });

            assertEquals(ResultCode.FAILED.getCode(), exception.getCode());
            assertEquals("创建私有群失败", exception.getMessage());
        }
    }

    @Test
    void testRegister_TeamMemberSaveFailed() {
        // 模拟：用户名不存在
        doReturn(null).when(userService).getOne(any(LambdaQueryWrapper.class));
        // 模拟：保存用户成功
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return true;
        }).when(userService).save(any(User.class));
        // 模拟：查询角色
        when(roleService.getByCode("ROLE_USER")).thenReturn(role);
        // 模拟：保存用户角色关联
        when(userRoleService.save(any(UserRole.class))).thenReturn(true);
        // 模拟：保存团队
        doAnswer(invocation -> {
            Team t = invocation.getArgument(0);
            t.setId(1L);
            return true;
        }).when(teamService).save(any(Team.class));
        // 模拟：保存团队成员失败
        when(teamMemberService.save(any(TeamMember.class))).thenReturn(false);

        // 执行测试并验证异常
        try (MockedStatic<PasswordUtil> passwordUtilMock = mockStatic(PasswordUtil.class)) {
            passwordUtilMock.when(() -> PasswordUtil.encode("password123")).thenReturn("encoded_password");
            
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.register(registerDTO);
            });

            assertEquals(ResultCode.FAILED.getCode(), exception.getCode());
            assertEquals("添加团队成员失败", exception.getMessage());
        }
    }

    @Test
    void testGetUserById_Success() {
        // 模拟：用户存在
        doReturn(savedUser).when(userService).getById(1L);

        // 执行测试
        var result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getNickname(), result.getNickname());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        // 模拟：用户不存在
        doReturn(null).when(userService).getById(1L);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals(ResultCode.NOTFOUND.getCode(), exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }
}
