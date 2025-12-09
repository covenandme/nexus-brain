package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexus.common.result.PageResult;
import com.nexus.system.entity.Team;

/**
 * 团队服务接口
 */
public interface TeamService extends IService<Team> {
    
    /**
     * 删除团队（级联删除相关数据）
     * @param id 团队ID
     * @return 是否删除成功
     */
    boolean removeById(java.io.Serializable id);
    
    /**
     * 获取用户加入的所有团队（分页）
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 团队分页结果
     */
    PageResult<Team> getMyTeams(Long userId, int pageNum, int pageSize);
}
