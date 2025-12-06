package com.nexus.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.common.result.ResultCode;
import com.nexus.system.entity.Team;
import com.nexus.system.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 团队控制器测试
 */
@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetTeamById_Success() throws Exception {
        // 模拟：团队存在
        when(teamService.getById(1L)).thenReturn(team);

        // 执行测试
        mockMvc.perform(get("/system/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("测试团队"))
                .andExpect(jsonPath("$.data.type").value(1))
                .andExpect(jsonPath("$.data.ownerId").value(1L));

        // 验证方法调用
        verify(teamService, times(1)).getById(1L);
    }

    @Test
    void testGetTeams_Success() throws Exception {
        // 模拟：分页查询
        @SuppressWarnings("unchecked")
        Page<Team> page = new Page<>(1, 10);
        page.setRecords(java.util.Collections.singletonList(team));
        page.setTotal(1L);
        when(teamService.page(any(Page.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/system/teams")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.current").value(1L))
                .andExpect(jsonPath("$.data.size").value(10L))
                .andExpect(jsonPath("$.data.total").value(1L));

        // 验证方法调用
        verify(teamService, times(1)).page(any(Page.class));
    }

    @Test
    void testGetTeams_DefaultParams() throws Exception {
        // 测试默认参数
        @SuppressWarnings("unchecked")
        Page<Team> page = new Page<>(1, 10);
        page.setRecords(java.util.Collections.emptyList());
        page.setTotal(0L);
        when(teamService.page(any(Page.class))).thenReturn(page);

        mockMvc.perform(get("/system/teams"))
                .andExpect(status().isOk());

        verify(teamService, times(1)).page(any(Page.class));
    }

    @Test
    void testCreateTeam_Success() throws Exception {
        // 模拟：创建成功
        when(teamService.save(any(Team.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/system/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 验证方法调用
        verify(teamService, times(1)).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_Success() throws Exception {
        // 模拟：更新成功
        when(teamService.updateById(any(Team.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/system/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 验证方法调用
        verify(teamService, times(1)).updateById(any(Team.class));
    }

    @Test
    void testDeleteTeam_Success() throws Exception {
        // 模拟：删除成功
        when(teamService.removeById(1L)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/system/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 验证方法调用
        verify(teamService, times(1)).removeById(1L);
    }
}
