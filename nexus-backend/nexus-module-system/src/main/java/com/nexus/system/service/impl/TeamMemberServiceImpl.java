package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.PageResult;
import com.nexus.common.result.ResultCode;
import com.nexus.system.entity.Team;
import com.nexus.system.entity.TeamMember;
import com.nexus.system.entity.User;
import com.nexus.system.mapper.TeamMapper;
import com.nexus.system.mapper.TeamMemberMapper;
import com.nexus.system.service.TeamMemberService;
import com.nexus.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户-团队关联服务实现类
 */
@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {
    
    @Autowired
    private TeamMapper teamMapper;

    
    /**
     * 团队角色常量：OWNER
     */
    private static final int TEAM_ROLE_OWNER = 1;
    
    /**
     * 团队角色常量：MEMBER
     */
    private static final int TEAM_ROLE_MEMBER = 2;
    
    @Override
    public boolean addMember(Long teamId, Long userId, Integer roleId, Long currentUserId) {
        // 参数校验
        if (teamId == null || userId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "团队ID和用户ID不能为空");
        }
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 默认角色为MEMBER
        if (roleId == null) {
            roleId = TEAM_ROLE_MEMBER;
        }
        
        // 检查团队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "团队不存在");
        }

        // 检查当前用户是否是团队所有者
        if (!team.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有团队所有者才能添加成员");
        }
        
        // 检查用户是否已经是团队成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId);
        TeamMember existingMember = this.getOne(wrapper);
        if (existingMember != null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户已经是团队成员");
        }
        
        // 添加成员
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        teamMember.setRoleId(roleId);
        return this.save(teamMember);
    }
    
    @Override
    public boolean removeMember(Long teamId, Long userId, Long currentUserId) {
        // 参数校验
        if (teamId == null || userId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "团队ID和用户ID不能为空");
        }
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 检查团队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "团队不存在");
        }
        
        // 检查当前用户是否是团队所有者
        if (!team.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有团队所有者才能移除成员");
        }
        
        // 不能移除团队所有者自己
        if (userId.equals(team.getOwnerId())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "不能移除团队所有者");
        }
        
        // 检查用户是否是团队成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId);
        TeamMember member = this.getOne(wrapper);
        if (member == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "用户不是团队成员");
        }
        
        // 移除成员
        return this.remove(wrapper);
    }
    
    @Override
    public boolean updateMemberRole(Long teamId, Long userId, Integer roleId, Long currentUserId) {
        // 参数校验
        if (teamId == null || userId == null || roleId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "团队ID、用户ID和角色ID不能为空");
        }
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 检查团队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "团队不存在");
        }
        
        // 检查当前用户是否是团队所有者
        if (!team.getOwnerId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有团队所有者才能更新成员角色");
        }
        
        // 不能修改团队所有者的角色
        if (userId.equals(team.getOwnerId())) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "不能修改团队所有者的角色");
        }
        
        // 检查用户是否是团队成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId);
        TeamMember member = this.getOne(wrapper);
        if (member == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "用户不是团队成员");
        }
        
        // 更新角色
        member.setRoleId(roleId);
        return this.updateById(member);
    }
    
    @Override
    public PageResult<TeamMember> getTeamMembers(Long teamId, int pageNum, int pageSize) {
        // 参数校验
        if (teamId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "团队ID不能为空");
        }
        
        // 检查团队是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "团队不存在");
        }
        
        // 分页查询
        Page<TeamMember> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        IPage<TeamMember> memberPage = this.page(page, wrapper);
        
        return PageResult.of(memberPage);
    }
}
