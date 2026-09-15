package com.campusfound;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CampusfoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                CampusfoundApplication.class,
                args
        );
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(
                TimeZone.getTimeZone("Asia/Kolkata")
        );
    }
}