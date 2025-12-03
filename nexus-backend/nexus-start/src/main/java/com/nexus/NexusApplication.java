package com.nexus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        // 核心修改：排除掉 OpenAI 和 PgVector 的自动配置
        // 这样启动时就不会检查 API Key 和 向量数据库连接了
        exclude = {
                org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class,
                org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration.class
        }
)
// 确保扫描到所有子模块的 Bean (Result, GlobalExceptionHandler 等)
@ComponentScan(basePackages = "com.nexus")
// 扫描所有 Mapper 接口
@MapperScan("com.nexus.*.mapper")
public class NexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
        System.out.println("----------------------------------------");
        System.out.println("--- NexusBrain Application Started! ---");
        System.out.println("----------------------------------------");
    }
}