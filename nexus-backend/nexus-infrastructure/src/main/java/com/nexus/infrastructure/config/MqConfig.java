package com.nexus.infrastructure.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter); // 确保能解析 JSON

        // 🚨 强制设置为手动签收模式！
        // 只有加了这行，你的 DocParseListener 里的 channel.basicAck 才会生效且不报错
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        // 可选：设置并发数（同时处理多少个文件）
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);

        return factory;
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
