package com.smartresume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartresume")
public class SmartResumeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartResumeApplication.class, args);
    }
}