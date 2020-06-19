package com.podo.study.toby.reactive.chapter5.resolve4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static private AtomicInteger COUNTER = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        final ExecutorService es = Executors.newFixedThreadPool(100);

        final RestTemplate rt = new RestTemplate();
        final String url = "http://localhost:8080/rest?idx={idx}";

        // 쓰레드 동기화, 동시에 100개 요청.
        final CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                final int idx = COUNTER.addAndGet(1);

                barrier.await(); // 블록킹, await()을 호출한 숫자가 초기화시 parites 숫자만큼 될때 블록킹 풀림.

                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                final String res = rt.getForObject(url, String.class, idx);

                sw.stop();

                log.info("Elapsed: {} -> {}, response= {}", idx, sw.getTotalTimeSeconds(), res);

                return null;
            });
        }

        final StopWatch main = new StopWatch();
        main.start();

        barrier.await();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();

        log.info("Total: {}", main.getTotalTimeSeconds());

    }
}
