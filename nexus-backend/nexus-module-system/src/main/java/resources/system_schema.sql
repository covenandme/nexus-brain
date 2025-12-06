-- 1. 用户表 (移除 account_tier)
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL COMMENT '主键ID',
                            `username` varchar(50) NOT NULL COMMENT '用户名',
                            `password` varchar(100) NOT NULL COMMENT '密码',
                            `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                            `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                            `create_time` datetime DEFAULT NULL,
                            `update_time` datetime DEFAULT NULL,
                            `deleted` tinyint DEFAULT 0,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`)
) COMMENT='系统用户表';

-- 2. 角色表 (新增)
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL COMMENT '角色ID',
                            `name` varchar(50) NOT NULL COMMENT '角色名称 (如: 普通用户, VIP会员)',
                            `code` varchar(50) NOT NULL COMMENT '角色编码 (如: ROLE_USER, ROLE_VIP)',
                            `description` varchar(100) DEFAULT NULL COMMENT '描述',
                            `create_time` datetime DEFAULT NULL,
                            `update_time` datetime DEFAULT NULL,
                            `deleted` tinyint DEFAULT 0,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_code` (`code`)
) COMMENT='系统角色表';

-- 3. 权限表 (新增)
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
                                  `id` bigint NOT NULL COMMENT '权限ID',
                                  `name` varchar(50) NOT NULL COMMENT '权限名称 (如: 创建团队)',
                                  `code` varchar(50) NOT NULL COMMENT '权限标识 (如: team:create)',
                                  `create_time` datetime DEFAULT NULL,
                                  `update_time` datetime DEFAULT NULL,
                                  `deleted` tinyint DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_code` (`code`)
) COMMENT='系统权限表';

-- 4. 用户-角色关联表 (新增)
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `id` bigint NOT NULL COMMENT '主键ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `create_time` datetime DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) COMMENT='用户-角色关联表';

-- 5. 角色-权限关联表 (新增)
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
                                       `id` bigint NOT NULL COMMENT '主键ID',
                                       `role_id` bigint NOT NULL COMMENT '角色ID',
                                       `permission_id` bigint NOT NULL COMMENT '权限ID',
                                       `create_time` datetime DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) COMMENT='角色-权限关联表';

DROP TABLE IF EXISTS `sys_team`;
CREATE TABLE `sys_team` (
                                 `id` bigint NOT NULL,
                                 `name` varchar(50) NOT NULL,
                                 `type` tinyint NOT NULL DEFAULT 1,
                                 `owner_id` bigint NOT NULL,
                                 `storage_quota` bigint DEFAULT 1024,
                                 `create_time` datetime DEFAULT NULL,
                                 `update_time` datetime DEFAULT NULL,
                                 `deleted` tinyint DEFAULT 0,
                                 PRIMARY KEY (`id`)
) COMMENT='团队表';

DROP TABLE IF EXISTS `sys_user_team`;
CREATE TABLE `sys_user_team` (
                                      `id` bigint NOT NULL,
                                      `user_id` bigint NOT NULL,
                                      `team_id` bigint NOT NULL,
                                      `role_id` int NOT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `update_time` datetime DEFAULT NULL,
                                      `deleted` tinyint DEFAULT 0,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_user_team` (`user_id`, `team_id`)
) COMMENT='用户-团队关联表';