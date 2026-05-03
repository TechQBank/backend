package com.qbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechQBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechQBankApplication.class, args);
    }
}
