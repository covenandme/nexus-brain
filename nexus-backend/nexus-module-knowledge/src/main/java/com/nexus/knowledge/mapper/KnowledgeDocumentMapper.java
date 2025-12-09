package com.nexus.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.knowledge.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文档 Mapper 接口
 */
@Mapper
public interface KnowledgeDocumentMapper extends BaseMapper<KnowledgeDocument> {
}
