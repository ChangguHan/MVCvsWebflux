package com.example.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class MvcApplication {
    public static final String PORT = "9001";


    public static void main(String[] args) {
        System.setProperty("server.port", PORT);
        System.setProperty("logging.level.org.springframework.web", "debug");

        SpringApplication.run(MvcApplication.class, args);
    }

    @RequiredArgsConstructor
    @RestController
    @Slf4j
    public static class TestController {
        private final TestService testService;

        @GetMapping("/test")
        public String getTest(Long num) throws InterruptedException {
            log.info("Controller {}", num);
            return testService.getTest(num);
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    @Service
    public static class TestService {

        public String getTest(Long num) throws InterruptedException {
            log.info("Service {}", num);
            Thread.sleep(1000);
            return "Test " + num;
        }
    }

}
