package com.nexus.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.exception.BusinessException;
import com.nexus.common.result.PageResult;
import com.nexus.common.result.ResultCode;
import com.nexus.system.dto.UserDto;
import com.nexus.system.dto.UserRegisterDTO;
import com.nexus.system.entity.*;
import com.nexus.system.mapper.UserMapper;
import com.nexus.system.service.*;
import com.nexus.system.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private TeamMemberService teamMemberService;
    
    /**
     * 团队角色常量：OWNER
     */
    private static final int TEAM_ROLE_OWNER = 1;
    
    /**
     * 团队类型常量：私有群
     */
    private static final int TEAM_TYPE_PRIVATE = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(UserRegisterDTO dto) {
        // 步骤1: 基本校验
        // 校验用户名是否已存在
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUsername, dto.getUsername());
        userWrapper.eq(User::getDeleted, 0);
        User existingUser = this.getOne(userWrapper);
        if (existingUser != null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户名已存在");
        }
        
        // 如果提供了邮箱，校验邮箱是否已存在
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail());
            emailWrapper.eq(User::getDeleted, 0);
            User existingEmailUser = this.getOne(emailWrapper);
            if (existingEmailUser != null) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "邮箱已被注册");
            }
        }
        
        // 步骤2: 插入 User，并加密密码
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        boolean userSaved = this.save(user);
        if (!userSaved) {
            throw new BusinessException(ResultCode.FAILED, "用户注册失败");
        }
        
        // 步骤3: 分配角色 - 查询 code='ROLE_USER' 的角色，并在 sys_user_role 表中插入关联记录
        Role role = roleService.getByCode("ROLE_USER");
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        boolean roleAssigned = userRoleService.save(userRole);
        if (!roleAssigned) {
            throw new BusinessException(ResultCode.FAILED, "角色分配失败");
        }
        
        // 步骤4: 初始化私有群 (Private Team)
        // 4.1 在 sys_team 插入一条 type=1 (私有群) 的记录，owner_id 为新用户 ID
        Team team = new Team();
        team.setName(dto.getUsername() + "的私有群");
        team.setType(TEAM_TYPE_PRIVATE);
        team.setOwnerId(user.getId());
        team.setStorageQuota(1024L); // 默认存储配额 1024MB
        boolean teamSaved = teamService.save(team);
        if (!teamSaved) {
            throw new BusinessException(ResultCode.FAILED, "创建私有群失败");
        }
        
        // 4.2 在 sys_user_team 插入关联记录，将新用户与该新群组绑定，role 设为 OWNER (role_id=1)
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(user.getId());
        teamMember.setTeamId(team.getId());
        teamMember.setRoleId(TEAM_ROLE_OWNER);
        boolean memberSaved = teamMemberService.save(teamMember);
        if (!memberSaved) {
            throw new BusinessException(ResultCode.FAILED, "添加团队成员失败");
        }
        
        return true;
    }

    public UserDto getUserById(Long id) {
       User user = super.getById(id);
       if(user == null) {
           throw new BusinessException(ResultCode.NOTFOUND,"用户不存在");
       }
       UserDto userDto = new UserDto();
       BeanUtils.copyProperties(user, userDto);
       return userDto;
    }

    public PageResult<UserDto> getAllUsers(int pageNum, int pageSize) {
        // 创建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        IPage<User> userPage = super.page(page);

        // 转换为DTO
        IPage<UserDto> dtoPage = userPage.convert(po -> {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(po, dto);
            return dto;
        });
        return PageResult.of(dtoPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户ID不能为空");
        }

        Long userId = Long.valueOf(id.toString());
        
        // 检查用户是否存在
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOTFOUND, "用户不存在");
        }

        // 步骤1: 删除用户的所有私有团队（个人空间）
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(Team::getOwnerId, userId);
        teamWrapper.eq(Team::getType, TEAM_TYPE_PRIVATE);
        teamWrapper.eq(Team::getDeleted, 0);
        java.util.List<Team> privateTeams = teamService.list(teamWrapper);

        for (Team team : privateTeams) {
            // 删除该团队的所有成员关联
            LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(TeamMember::getTeamId, team.getId());
            memberWrapper.eq(TeamMember::getDeleted, 0);
            teamMemberService.remove(memberWrapper);

            // 删除团队本身
            teamService.removeById(team.getId());
        }

        // 步骤2: 删除用户的所有团队关联（包括在其他团队中的成员关系）
        LambdaQueryWrapper<TeamMember> userTeamWrapper = new LambdaQueryWrapper<>();
        userTeamWrapper.eq(TeamMember::getUserId, userId);
        userTeamWrapper.eq(TeamMember::getDeleted, 0);
        teamMemberService.remove(userTeamWrapper);

        // 步骤3: 删除用户的所有角色关联
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        userRoleWrapper.eq(UserRole::getDeleted, 0);
        userRoleService.remove(userRoleWrapper);

        // 步骤4: 最后删除用户本身
        return super.removeById(userId);
    }
}