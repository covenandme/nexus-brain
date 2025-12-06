package com.nexus.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.common.result.PageResult;
import com.nexus.common.result.Result;
import com.nexus.system.entity.Team;
import com.nexus.system.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 团队控制器
 */
@RestController
@RequestMapping("/system/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * 根据ID获取团队信息
     */
    @GetMapping("/{id}")
    public Result<Team> getTeamById(@PathVariable Long id) {
        Team team = teamService.getById(id);
        return Result.success(team);
    }

    /**
     * 分页查询团队列表
     */
    @GetMapping
    public Result<PageResult<Team>> getTeams(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        Page<Team> page = new Page<>(pageNum, pageSize);
        IPage<Team> teamPage = teamService.page(page);
        return Result.success(PageResult.of(teamPage));
    }

    /**
     * 创建团队
     */
    @PostMapping
    public Result<Boolean> createTeam(@RequestBody Team team) {
        boolean saved = teamService.save(team);
        return Result.success(saved);
    }

    /**
     * 更新团队
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        team.setId(id);
        boolean updated = teamService.updateById(team);
        return Result.success(updated);
    }

    /**
     * 删除团队
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeam(@PathVariable Long id) {
        boolean removed = teamService.removeById(id);
        return Result.success(removed);
    }
}
