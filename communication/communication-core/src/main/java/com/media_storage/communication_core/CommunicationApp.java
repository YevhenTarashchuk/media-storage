package com.media_storage.communication_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.media_storage.*")
public class CommunicationApp {
    public static void main(String[] args) {
        SpringApplication.run(CommunicationApp.class, args);
    }
}