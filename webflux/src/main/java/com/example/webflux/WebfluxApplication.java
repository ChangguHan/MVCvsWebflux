package com.example.webflux;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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

        @GetMapping("/persons/json")
        public Flux<Person> getPersons(Long num) throws InterruptedException {
            return Flux.fromStream(this::prepareStream)
                       .doOnNext(person -> LOGGER.info("Server produces: {}", person));
        }

        private Stream<Person> prepareStream() {
            return Stream.of(
                    new Person(1, "Name01", "Surname01", 11),
                    new Person(2, "Name02", "Surname02", 22),
                    new Person(3, "Name03", "Surname03", 33),
                    new Person(4, "Name04", "Surname04", 44),
                    new Person(5, "Name05", "Surname05", 55),
                    new Person(6, "Name06", "Surname06", 66),
                    new Person(7, "Name07", "Surname07", 77),
                    new Person(8, "Name08", "Surname08", 88),
                    new Person(9, "Name09", "Surname09", 99)
            );
        }

    }

    @Data
    @AllArgsConstructor
    public static class Person {
        private int id;
        private String name;
        private String surName;
        private int age;
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
