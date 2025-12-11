package com.nexus.rag.listener;

import com.nexus.common.dto.mq.DocParseMsg;
import com.nexus.infrastructure.config.MqConfig;
import com.nexus.rag.service.RagService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 文档解析监听器
 */
@Slf4j
@Component
public class DocParseListener {
    
    @Autowired
    private RagService ragService;
    
    /**
     * 监听文档解析队列（手动 ACK 模式）
     * 
     * @param msg 文档解析消息
     * @param channel RabbitMQ 通道
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = MqConfig.QUEUE_DOC_PARSE)
    public void handle(DocParseMsg msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("收到解析任务: {}", msg);
        
        try {
            // 调用 RagService 进行文档解析和向量化
            ragService.parseAndStore(msg.getDocId());
            
            // 手动确认消息（告诉 MQ 消息已成功处理，可以删除）
            channel.basicAck(deliveryTag, false);
            log.info("消息确认成功: docId={}, deliveryTag={}", msg.getDocId(), deliveryTag);
            
        } catch (Exception e) {
            log.error("解析任务失败: docId={}, deliveryTag={}", msg.getDocId(), deliveryTag, e);
            
            try {
                // 拒绝消息并丢弃（不重新入队）
                // 参数说明：deliveryTag, multiple=false, requeue=false
                // requeue=false 表示不重新入队，避免无限重试导致队列阻塞
                channel.basicNack(deliveryTag, false, false);
                log.warn("消息已拒绝并丢弃: docId={}, deliveryTag={}", msg.getDocId(), deliveryTag);
            } catch (Exception nackException) {
                log.error("拒绝消息时发生异常: docId={}", msg.getDocId(), nackException);
            }
        }
    }
}
