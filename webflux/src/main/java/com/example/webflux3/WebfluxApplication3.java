package com.example.webflux3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFlux
public class WebfluxApplication3 {

    public static final String PORT = "9004";


    public static void main(String[] args) {
        System.setProperty("server.port", PORT);
        System.setProperty("logging.level.org.springframework.web", "debug");

        SpringApplication.run(WebfluxApplication3.class, args);
    }

    @RequiredArgsConstructor
    @RestController
    @Slf4j
    public static class TestController {
        private final TestService testService;

        @GetMapping("/test")
        public Mono<String> getTest(Long num) throws InterruptedException {
            return testService.getTest(num)
                    .log();
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    @Service
    public static class TestService {

        public Mono<String> getTest(Long num) throws InterruptedException {
            return Mono.fromSupplier(() -> "Test " + num)
                    .log();
        }
    }

}
