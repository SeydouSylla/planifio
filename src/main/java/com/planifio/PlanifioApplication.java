package com.planifio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlanifioApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanifioApplication.class, args);
    }
}
