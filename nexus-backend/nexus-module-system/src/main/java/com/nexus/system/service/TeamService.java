package com.nexus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
}
