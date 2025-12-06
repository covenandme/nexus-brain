package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.system.entity.Team;
import com.nexus.system.mapper.TeamMapper;
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
 * 团队服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamMapper teamMapper;

    @Spy
    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("测试团队");
        team.setType(1);
        team.setOwnerId(1L);
        team.setStorageQuota(1024L);
    }

    @Test
    void testSave_Success() {
        // 模拟：保存成功
        doReturn(true).when(teamService).save(any(Team.class));

        // 执行测试
        boolean result = teamService.save(team);

        // 验证结果
        assertTrue(result);
        verify(teamService, times(1)).save(any(Team.class));
    }

    @Test
    void testGetById_Success() {
        // 模拟：团队存在
        doReturn(team).when(teamService).getById(1L);

        // 执行测试
        Team result = teamService.getById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(team.getId(), result.getId());
        assertEquals(team.getName(), result.getName());
        assertEquals(team.getType(), result.getType());
        assertEquals(team.getOwnerId(), result.getOwnerId());
    }

    @Test
    void testUpdateById_Success() {
        // 模拟：更新成功
        doReturn(true).when(teamService).updateById(any(Team.class));

        // 执行测试
        boolean result = teamService.updateById(team);

        // 验证结果
        assertTrue(result);
        verify(teamService, times(1)).updateById(any(Team.class));
    }

    @Test
    void testRemoveById_Success() {
        // 模拟：删除成功
        doReturn(true).when(teamService).removeById(1L);

        // 执行测试
        boolean result = teamService.removeById(1L);

        // 验证结果
        assertTrue(result);
        verify(teamService, times(1)).removeById(1L);
    }

    @Test
    void testPage_Success() {
        // 模拟：分页查询
        @SuppressWarnings("unchecked")
        Page<Team> page = new Page<>(1, 10);
        page.setRecords(java.util.Collections.singletonList(team));
        page.setTotal(1L);
        doReturn(page).when(teamService).page(any(Page.class));

        // 执行测试
        IPage<Team> result = teamService.page(page);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(team.getId(), result.getRecords().get(0).getId());
    }
}
