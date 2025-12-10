package com.nexus.common.dto.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * 文档解析消息体
 */
@Data
public class DocParseMsg implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文档ID
     */
    private Long docId;
    
    /**
     * 发送时间戳
     */
    private Long timestamp;
}
