package com.podo.study.toby.reactive.chapter4.springex4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tomcat max thread 1개 실행, application.properties 설정
 *
 * 워크쓰레드가 필요없음.
 */
@Slf4j
public class LoadTest {
    static private AtomicInteger COUNTER =new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(100);

        final RestTemplate rt = new RestTemplate();
        final String url = "http://localhost:8080/dr";

        StopWatch main = new StopWatch();
        main.start();

        for(int i =0; i < 100; i++){
            es.execute(() -> {
                final int idx = COUNTER.addAndGet(1);
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                rt.getForObject(url, String.class);

                sw.stop();
                log.info("Elapsed: {} -> {}", idx, sw.getTotalTimeSeconds());
            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();

        log.info("Total: {}", main.getTotalTimeSeconds());

    }
}
