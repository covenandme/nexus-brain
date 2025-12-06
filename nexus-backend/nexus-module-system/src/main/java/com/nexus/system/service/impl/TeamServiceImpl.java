package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.system.entity.Team;
import com.nexus.system.mapper.TeamMapper;
import com.nexus.system.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * 团队服务实现类
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
}
