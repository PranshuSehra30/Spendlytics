package com.pranshudev.spendlytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpendlyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpendlyticsApplication.class, args);
    }

}
