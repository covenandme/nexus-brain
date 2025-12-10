package com.nexus.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 */
@Configuration
public class MqConfig {
    
    /**
     * 知识库交换机
     */
    public static final String EXCHANGE_KNOWLEDGE = "nexus.exchange.knowledge";
    
    /**
     * 文档解析队列
     */
    public static final String QUEUE_DOC_PARSE = "nexus.queue.doc.parse";
    
    /**
     * 文档解析路由键
     */
    public static final String ROUTING_KEY_PARSE = "nexus.key.doc.parse";
    
    /**
     * 消息转换器 - 使用 Jackson 将对象转为 JSON
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * 定义知识库交换机（持久化）
     */
    @Bean
    public DirectExchange knowledgeExchange() {
        return new DirectExchange(EXCHANGE_KNOWLEDGE, true, false);
    }
    
    /**
     * 定义文档解析队列（持久化）
     */
    @Bean
    public Queue docParseQueue() {
        return new Queue(QUEUE_DOC_PARSE, true);
    }
    
    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding docParseBinding(Queue docParseQueue, DirectExchange knowledgeExchange) {
        return BindingBuilder.bind(docParseQueue)
                .to(knowledgeExchange)
                .with(ROUTING_KEY_PARSE);
    }
}
