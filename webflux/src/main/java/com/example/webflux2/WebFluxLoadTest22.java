package com.example.webflux2;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

// 왜 블록킹으로 실행이되지?
@Slf4j
public class WebFluxLoadTest22 {
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        String url = "http://localhost:" + WebfluxApplication2.PORT + "/test";
        int requestNum = 10; // 웹플럭스 디폴트 스레드 개수, 코어 * 2
        ExecutorService es = Executors.newFixedThreadPool(requestNum);
        WebClient rt = WebClient.builder().build();
        StopWatch mainsw = new StopWatch();
        mainsw.start();

        CyclicBarrier barrier = new CyclicBarrier(requestNum);

        for(int i=0; i<requestNum; i++) {
            int finalI = i;
            es.submit(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                StopWatch sw = new StopWatch();
                sw.start();
                rt.get().uri(url, Map.of("num", finalI)).exchangeToMono(e -> e.bodyToMono(String.class));
                sw.stop();
                log.info("Elapse: {}, {}", finalI, sw.getTotalTimeMillis());
            });
        }
        mainsw.stop();
        log.info("Total: {}", mainsw.getTotalTimeMillis());


        es.shutdown();
        es.awaitTermination(5, TimeUnit.SECONDS);
    }
}
