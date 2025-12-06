package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.system.entity.TeamMember;
import com.nexus.system.mapper.TeamMemberMapper;
import com.nexus.system.service.TeamMemberService;
import org.springframework.stereotype.Service;

/**
 * 用户-团队关联服务实现类
 */
@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {
}
