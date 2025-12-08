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
import com.nexus.system.mapper.TeamMapper;
import com.nexus.system.service.TeamMemberService;
import com.nexus.system.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 团队服务实现类
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    
    @Autowired
    private TeamMemberService teamMemberService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "团队ID不能为空");
        }
        
        Long teamId = Long.valueOf(id.toString());
        
        // 检查团队是否存在
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "团队不存在");
        }
        
        // 步骤1: 删除团队的所有成员关联
        // MyBatis-Plus 的 @TableLogic 会自动过滤已删除的记录，无需手动判断 deleted
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, teamId);
        teamMemberService.remove(memberWrapper);
        
        // 步骤2: 删除团队本身
        return super.removeById(teamId);
    }
    
    @Override
    public PageResult<Team> getMyTeams(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 查询用户加入的所有团队ID
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getUserId, userId)
                     .select(TeamMember::getTeamId);
        java.util.List<Long> teamIds = teamMemberService.listObjs(memberWrapper, obj -> (Long) obj);
        
        // 如果用户没有加入任何团队，返回空结果
        if (teamIds.isEmpty()) {
            return PageResult.of(new Page<>(pageNum, pageSize));
        }
        
        // 分页查询团队信息
        Page<Team> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.in(Team::getId, teamIds);
        IPage<Team> teamPage = this.page(page, teamWrapper);
        
        return PageResult.of(teamPage);
    }
}
