package com.podo.study.toby.reactive.chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Future 비동기 작업을 수행했고, 그 결과를 가지고 있는 것
 * <p>
 * 쓰레드를 사용하는 것은 큰 비용.
 * 쓰레드풀과 같은 재사용을 이용함.
 * <p>
 * Runnable : 호출
 * Callable : 리턴값 받음, 예외 던짐
 *
 * 비동기 작업을 가져오는 수단
 * - Future
 * - Callback
 */

@Slf4j
public class FutureEx {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final ExecutorService es = Executors.newSingleThreadExecutor();

        final Future<String> future = es.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });

        // future.get()을 호출하면,
        // 결과가 나올때까지 블로킹함.
        // 추가 정보 : 결과가 나올때까지 기다림 블로킹, 결과가 나올때까지 기다리지 않음 논블로킹
        log.debug(future.get()); //블로킹

        //블로킹이기 때문에 더이상, 쓰레드의 의미가 없어짐. 쓰레드가 많다고 더 빠르지 않음
        log.debug("Exit");

        //log.debug(future.isDone()); // while(future.isDone())

    }
}

