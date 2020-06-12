package com.podo.study.toby.reactive.chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 콜백 구현,
 * 그러나 부족, 오버라이딩해야한다.
 */

@Slf4j
public class FutureTaskEx2 { //Callback

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService es = Executors.newSingleThreadExecutor();
        final FutureTask<String> futureTask = new FutureTask<String>(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        }){
            @Override
            protected void done() { // 비동기 작업이 완료되면 수행되는 메소드
                try {
                    log.debug("{}", this.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        es.execute(futureTask);

        Thread.sleep(2100);
        log.debug("Exit");

    }
}

