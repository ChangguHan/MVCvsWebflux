package com.example.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WebfluxApplication {

    public static final String PORT = "9002";


    public static void main(String[] args) {
        System.setProperty("server.port", PORT);
        System.setProperty("logging.level.org.springframework.web", "debug");

        SpringApplication.run(WebfluxApplication.class, args);
    }

    @RequiredArgsConstructor
    @RestController
    @Slf4j
    public static class TestController {
        private final TestService testService;
        private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);


        @GetMapping("/test")
        public Mono<String> getTest(Long num) throws InterruptedException {
            log.info("Controller {}", num);
            return testService.getTest(num);
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    @Service
    public static class TestService {

        public Mono<String> getTest(Long num) throws InterruptedException {
            log.info("Service {}", num);
            Thread.sleep(1000);
            return Mono.fromSupplier(() -> "Test " + num);
        }
    }

}
