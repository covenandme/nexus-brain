package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.common.result.PageResult;
import com.nexus.system.entity.TeamMember;

/**
 * 用户-团队关联服务接口
 */
public interface TeamMemberService extends IService<TeamMember> {
    
    /**
     * 添加团队成员
     * @param teamId 团队ID
     * @param userId 用户ID
     * @param roleId 团队角色ID
     * @param currentUserId 当前操作用户ID
     * @return 是否添加成功
     */
    boolean addMember(Long teamId, Long userId, Integer roleId, Long currentUserId);
    
    /**
     * 移除团队成员
     * @param teamId 团队ID
     * @param userId 用户ID
     * @param currentUserId 当前操作用户ID
     * @return 是否移除成功
     */
    boolean removeMember(Long teamId, Long userId, Long currentUserId);
    
    /**
     * 更新成员角色
     * @param teamId 团队ID
     * @param userId 用户ID
     * @param roleId 新角色ID
     * @param currentUserId 当前操作用户ID
     * @return 是否更新成功
     */
    boolean updateMemberRole(Long teamId, Long userId, Integer roleId, Long currentUserId);
    
    /**
     * 分页查询团队成员
     * @param teamId 团队ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 成员分页结果
     */
    PageResult<TeamMember> getTeamMembers(Long teamId, int pageNum, int pageSize);
}
