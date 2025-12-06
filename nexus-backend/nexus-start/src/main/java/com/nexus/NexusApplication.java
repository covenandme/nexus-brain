package com.nexus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.nexus.**.mapper")
public class NexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
        System.out.println("----------------------------------------");
        System.out.println("--- NexusBrain Application Started! ---");
        System.out.println("----------------------------------------");
    }
}