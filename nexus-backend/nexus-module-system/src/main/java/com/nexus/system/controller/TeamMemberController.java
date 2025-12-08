package com.nexus.system.controller;

import com.nexus.common.result.PageResult;
import com.nexus.common.result.Result;
import com.nexus.common.security.LoginUser;
import com.nexus.infrastructure.security.CurrentUser;
import com.nexus.system.entity.TeamMember;
import com.nexus.system.service.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 团队成员控制器
 */
@Tag(name = "团队成员管理", description = "团队成员相关的接口，包括添加、查询、更新、删除等操作")
@RestController
@RequestMapping("/system/team-members")
public class TeamMemberController {

    @Autowired
    private TeamMemberService teamMemberService;

    /**
     * 分页查询团队成员列表
     */
    @Operation(summary = "分页查询团队成员", description = "根据团队ID分页查询团队成员列表")
    @GetMapping
    public Result<PageResult<TeamMember>> getTeamMembers(
            @Parameter(description = "团队ID", required = true)
            @RequestParam Long teamId,
            @Parameter(description = "页码，默认1", example = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小，默认10", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<TeamMember> members = teamMemberService.getTeamMembers(teamId, pageNum, pageSize);
        return Result.success(members);
    }

    /**
     * 根据ID获取团队成员信息
     */
    @Operation(summary = "根据ID查询团队成员", description = "根据团队成员ID查询详细信息")
    @GetMapping("/{id}")
    public Result<TeamMember> getTeamMemberById(
            @Parameter(description = "团队成员ID", required = true)
            @PathVariable Long id) {
        TeamMember member = teamMemberService.getById(id);
        return Result.success(member);
    }

    /**
     * 添加团队成员
     */
    @Operation(summary = "添加团队成员", description = "向指定团队添加新成员，只有团队所有者才能操作")
    @PostMapping
    public Result<Boolean> addTeamMember(
            @CurrentUser LoginUser loginUser,
            @Parameter(description = "团队ID", required = true)
            @RequestParam Long teamId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "团队角色ID: 1-OWNER, 2-MEMBER", example = "2")
            @RequestParam(required = false) Integer roleId) {
        boolean result = teamMemberService.addMember(teamId, userId, roleId, loginUser.getUserId());
        return Result.success(result);
    }

    /**
     * 更新团队成员角色
     */
    @Operation(summary = "更新团队成员角色", description = "更新指定成员的团队角色，只有团队所有者才能操作")
    @PutMapping("/{id}/role")
    public Result<Boolean> updateMemberRole(
            @CurrentUser LoginUser loginUser,
            @Parameter(description = "团队成员ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "团队ID", required = true)
            @RequestParam Long teamId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "新角色ID: 1-OWNER, 2-MEMBER", required = true)
            @RequestParam Integer roleId) {
        boolean result = teamMemberService.updateMemberRole(teamId, userId, roleId, loginUser.getUserId());
        return Result.success(result);
    }

    /**
     * 移除团队成员
     */
    @Operation(summary = "移除团队成员", description = "从团队中移除指定成员，只有团队所有者才能操作")
    @DeleteMapping
    public Result<Boolean> removeMember(
            @CurrentUser LoginUser loginUser,
            @Parameter(description = "团队ID", required = true)
            @RequestParam Long teamId,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        boolean result = teamMemberService.removeMember(teamId, userId, loginUser.getUserId());
        return Result.success(result);
    }

    /**
     * 根据ID删除团队成员
     */
    @Operation(summary = "根据ID删除团队成员", description = "根据团队成员ID删除团队成员（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteById(
            @Parameter(description = "团队成员ID", required = true)
            @PathVariable Long id) {
        boolean removed = teamMemberService.removeById(id);
        return Result.success(removed);
    }
}
