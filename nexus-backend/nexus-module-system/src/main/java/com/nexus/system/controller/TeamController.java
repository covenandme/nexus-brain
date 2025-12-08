package com.nexus.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.common.result.PageResult;
import com.nexus.common.result.Result;
import com.nexus.common.security.LoginUser;
import com.nexus.infrastructure.security.CurrentUser;
import com.nexus.system.entity.Team;
import com.nexus.system.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 团队控制器
 */
@Tag(name = "团队管理", description = "团队相关的接口，包括创建、查询、更新、删除等操作")
@RestController
@RequestMapping("/system/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * 获取当前用户的所有团队
     */
    @Operation(summary = "获取我的团队", description = "分页查询当前登录用户加入的所有团队")
    @GetMapping("/me")
    public Result<PageResult<Team>> getMyTeams(
            @CurrentUser LoginUser loginUser,
            @Parameter(description = "页码，默认1", example = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小，默认10", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Team> teams = teamService.getMyTeams(loginUser.getUserId(), pageNum, pageSize);
        return Result.success(teams);
    }

    /**
     * 根据ID获取团队信息
     */
    @Operation(summary = "根据ID查询团队", description = "根据团队ID查询团队详细信息")
    @GetMapping("/{id}")
    public Result<Team> getTeamById(
            @Parameter(description = "团队ID", required = true)
            @PathVariable Long id) {
        Team team = teamService.getById(id);
        return Result.success(team);
    }

    /**
     * 分页查询团队列表
     */
    @Operation(summary = "分页查询团队列表", description = "分页查询所有团队信息")
    @GetMapping
    public Result<PageResult<Team>> getTeams(
            @Parameter(description = "页码，默认1", example = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小，默认10", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Team> page = new Page<>(pageNum, pageSize);
        IPage<Team> teamPage = teamService.page(page);
        return Result.success(PageResult.of(teamPage));
    }

    /**
     * 创建团队
     */
    @Operation(summary = "创建团队", description = "创建新的团队")
    @PostMapping
    public Result<Boolean> createTeam(@RequestBody Team team) {
        boolean saved = teamService.save(team);
        return Result.success(saved);
    }

    /**
     * 更新团队
     */
    @Operation(summary = "更新团队", description = "根据团队ID更新团队信息")
    @PutMapping("/{id}")
    public Result<Boolean> updateTeam(
            @Parameter(description = "团队ID", required = true)
            @PathVariable Long id,
            @RequestBody Team team) {
        team.setId(id);
        boolean updated = teamService.updateById(team);
        return Result.success(updated);
    }

    /**
     * 删除团队
     */
    @Operation(summary = "删除团队", description = "根据团队ID删除团队（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeam(
            @Parameter(description = "团队ID", required = true)
            @PathVariable Long id) {
        boolean removed = teamService.removeById(id);
        return Result.success(removed);
    }
}
