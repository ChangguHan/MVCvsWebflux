package com.example.webflux2;

import java.time.Duration;

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
public class WebfluxApplication2 {

    public static final String PORT = "9003";


    public static void main(String[] args) {
        System.setProperty("server.port", PORT);
//        System.setProperty("reactor.netty.ioWorkerCount", "2");
        System.setProperty("logging.level.org.springframework.web", "debug");

        SpringApplication.run(WebfluxApplication2.class, args);
    }

    @RequiredArgsConstructor
    @RestController
    @Slf4j
    public static class TestController {
        private final TestService testService;

        @GetMapping("/test")
        public Mono<String> getTest(Long num) throws InterruptedException {
//            log.info("Controller {}", num);
            return testService.getTest(num)
                    .log();
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    @Service
    public static class TestService {

        public Mono<String> getTest(Long num) throws InterruptedException {
//            log.info("Service {}", num);
//            Thread.sleep(1000);
            return Mono.fromSupplier(() -> "Test " + num)
                    .delayElement(Duration.ofSeconds(1));

        }
    }

}
