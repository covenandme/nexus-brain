package com.nexus.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.knowledge.entity.KnowledgeFolder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库目录 Mapper 接口
 */
@Mapper
public interface KnowledgeFolderMapper extends BaseMapper<KnowledgeFolder> {
}
