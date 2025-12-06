package com.nexus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.system.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-团队关联Mapper接口
 */
@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
}
