package com.podo.study.toby.reactive.chapter4.springex2;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @Async 를 사용하여, 비즈니스 로직 분리.
 *
 * Callback 방법.
 * ListenableFuture 방법. // Spring 4.0
 */

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class SpringChapter4Application2 {

    @Component
    public static class MyService {
        @Async
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext c = SpringApplication.run(SpringChapter4Application2.class, args)) {

        }
    }

    private final MyService myService;

    @Bean
    public ThreadPoolTaskExecutor threadPoolExecutor(){
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        threadPoolTaskExecutor.setQueueCapacity(200);
        threadPoolTaskExecutor.setThreadNamePrefix("my-thread-");
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    @Bean
    public ApplicationRunner run() {
        return args -> {
            log.info("run()");
            final ListenableFuture<String> future = myService.hello();
            future.addCallback(s -> log.info("{}", s), e -> log.error("", e));
            log.info("exit {}", future.isDone());
        };
    }
}
