package com.nexus.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.knowledge.entity.KnowledgeDataset;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库数据集 Mapper 接口
 */
@Mapper
public interface KnowledgeDatasetMapper extends BaseMapper<KnowledgeDataset> {
}
