-- ----------------------------
-- 1. 知识库/数据集表 (knowledge_dataset)
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_dataset`;
CREATE TABLE `knowledge_dataset` (
                                     `id` bigint NOT NULL COMMENT '主键ID',
                                     `team_id` bigint NOT NULL COMMENT '所属团队ID',
                                     `name` varchar(64) NOT NULL COMMENT '知识库名称',
                                     `description` varchar(512) DEFAULT NULL COMMENT '描述',
                                     `avatar` varchar(255) DEFAULT NULL COMMENT '封面/图标URL',

                                     `create_user` bigint NOT NULL COMMENT '创建人ID',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `deleted` bigint DEFAULT 0 COMMENT '删除标记（0-未删除，删除时设置为当前记录的id值）',

                                     PRIMARY KEY (`id`),
    -- 核心变化：同一个团队下，未删除的知识库不能重名
                                     UNIQUE KEY `uk_team_name_deleted` (`team_id`, `name`, `deleted`),
                                     KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库数据集表';

-- ----------------------------
-- 2. 知识库目录表 (knowledge_folder)
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_folder`;
CREATE TABLE `knowledge_folder` (
                                    `id` bigint NOT NULL COMMENT '主键ID',
                                    `dataset_id` bigint NOT NULL COMMENT '所属数据集ID',
                                    `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父目录ID (0表示根目录)',
                                    `name` varchar(64) NOT NULL COMMENT '目录名称',
                                    `sort` int DEFAULT '0' COMMENT '排序号',

                                    `create_user` bigint NOT NULL COMMENT '创建人ID',
                                    `create_time` datetime DEFAULT NULL,
                                    `update_time` datetime DEFAULT NULL,
                                    `deleted` bigint DEFAULT 0 COMMENT '删除标记（0-未删除，删除时设置为当前记录的id值）',
                                    PRIMARY KEY (`id`),
    -- 核心变化：同一个父目录下，未删除的子目录不能重名
                                    UNIQUE KEY `uk_parent_name_deleted` (`parent_id`, `name`, `deleted`),
                                    KEY `idx_dataset` (`dataset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库目录表';

-- ----------------------------
-- 3. 知识库文档表 (knowledge_document)
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_document`;
CREATE TABLE `knowledge_document` (
                                      `id` bigint NOT NULL COMMENT '主键ID',
                                      `dataset_id` bigint NOT NULL COMMENT '所属数据集ID',
                                      `folder_id` bigint NOT NULL DEFAULT '0' COMMENT '所属目录ID (0表示在根目录下)',

                                      `name` varchar(255) NOT NULL COMMENT '文件名(包含后缀)',
                                      `size` bigint NOT NULL COMMENT '文件大小(字节)',
                                      `suffix` varchar(20) NOT NULL COMMENT '文件后缀',
                                      `minio_object_name` varchar(512) NOT NULL COMMENT 'MinIO存储路径',
                                      `content_hash` varchar(64) DEFAULT NULL COMMENT '文件Hash',

    -- RAG 状态
                                      `status` tinyint NOT NULL DEFAULT '0' COMMENT '解析状态: 0-待解析, 1-解析中, 2-完成, 3-失败',
                                      `vector_status` tinyint NOT NULL DEFAULT '0' COMMENT '向量化状态: 0-未, 1-完',
                                      `parse_error_msg` text COMMENT '失败原因',

                                      `create_user` bigint NOT NULL COMMENT '创建人ID',
                                      `create_time` datetime DEFAULT NULL,
                                      `update_time` datetime DEFAULT NULL,
                                      `deleted` bigint DEFAULT 0 COMMENT '删除标记（0-未删除，删除时设置为当前记录的id值）',

                                      PRIMARY KEY (`id`),
    -- 核心变化：同一个文件夹下，未删除的文件不能重名
                                      UNIQUE KEY `uk_folder_name_deleted` (`folder_id`, `name`, `deleted`),
                                      KEY `idx_dataset_status` (`dataset_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文档表';