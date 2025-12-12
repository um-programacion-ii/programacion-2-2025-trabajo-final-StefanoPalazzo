package com.stefanopalazzo.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner commandLineRunner(
            org.springframework.context.ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");
            String[] beanNames = ctx.getBeanDefinitionNames();
            java.util.Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                if (beanName.contains("Controller") || beanName.contains("Resource")) {
                    System.out.println(beanName);
                }
            }
        };
    }
}
