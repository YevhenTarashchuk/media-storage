package com.media_storage.media_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.media_storage.*")
public class MediaCoreApp {

    public static void main(String[] args) {
        SpringApplication.run(MediaCoreApp.class, args);
    }
}
