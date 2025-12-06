package com.nexus.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 模块测试专用的启动类
 * 作用：为 @WebMvcTest 提供 @SpringBootConfiguration 锚点
 */
@SpringBootApplication(scanBasePackages = "com.nexus.system")
public class SystemModuleTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemModuleTestApplication.class, args);
    }
}