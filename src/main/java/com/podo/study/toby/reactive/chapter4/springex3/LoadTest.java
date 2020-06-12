package com.podo.study.toby.reactive.chapter4.springex3;

import javafx.scene.paint.Stop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tomcat max thread 20개 실행 application.properties 설정
 *
 * 의문.
 * 서버전체를 생각하면
 * 결국 서블릿스레드 100개나 OR 작업스레드 100개가 무슨차이지?
 * -> 맞음, 결국 서블릿스레드에서 머무는 작업을 줄이는거임
 *
 * 쓰레드풀에 갯수가 100개정도인데
 * 긴 작업을 수행하는게 20개가있다. 응담이 늦어도됨 -> 별도의 작업쓰레드
 * 그러는 사이에 빠르게 처리해서 응답해야하는 경우, 서블릿스레드의 활용도가 높아진다.
 */
@Slf4j
public class LoadTest {
    static private AtomicInteger COUNTER =new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(100);

        final RestTemplate rt = new RestTemplate();
//        final String url = "http://localhost:8080/async";  // 10초 걸림
        String url = "http://localhost:8080/callable";  //  2초 걸림

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
