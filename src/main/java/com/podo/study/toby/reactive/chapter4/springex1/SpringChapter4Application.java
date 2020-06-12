package com.podo.study.toby.reactive.chapter4.springex1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @Async 를 사용하여, 비즈니스 로직 분리.
 *
 * Future 방법.
 */

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class SpringChapter4Application {

    @Component
    public static class MyService {
        @Async
        public Future<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext c = SpringApplication.run(SpringChapter4Application.class, args)) {

        }
    }

    private final MyService myService;

    @Bean
    public ApplicationRunner run() {
        return args -> {
            log.info("run()");
            final Future<String> future = myService.hello();
            log.info("exit : {}", future.isDone());
            log.info("result : {}", future.get());
        };
    }
}
