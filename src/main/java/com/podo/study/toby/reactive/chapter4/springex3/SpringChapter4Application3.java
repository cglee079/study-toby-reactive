package com.podo.study.toby.reactive.chapter4.springex3;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/**
 * 쓰레드 블로킹 -> 컨텍스트 스위치 ( 불필요한 cpu 자원 소모)
 * 서블릿은 기본적으로 블로킹 구조
 */

/**
 * NIO Connector 지원이전에, 톰캣에서 넘어오는 쓰레드에 하나의 서블렛 쓰레드 매칭
 * 1  st1
 * 2  st2
 * 3  st3
 * 4  st4
 */

/** 톰캣에 NIO Connector가 요청을 잡음, 실행시키는 시점에 쓰레드에 맞추어 서블릿 쓰레드 생성 -> 큰 의미 없음
 * 쓰레드풀 사이즈만큼 작업중이다 -> 큐에 쌓임 (응답시간 증가) -> 큐도 꽉참 -> 서비스에러 발생
 * 1                      st1 (ServletThread) -> req -> Work -> res
 * 2                      st2
 * 3   NIO Connector     st3
 * 4                      st4
 *
 * -> Work 가 동작하는 동안에, 서블릿쓰레드를 계속 들고있음.
 * -> Work 가 동작하는 동안, 서블릿쓰레드를 반환하고, 또다른 커넥션을 받으면 어떨까?
 * -> 이 아이디어로 - Servlet 3.0 비동기 서블릿 지원
 */

/**
 * 그럼 WorkThread를 별도의 작업쓰레드로 사용한다면, 응답은 어떻게 하는가?
 * -> 작업 쓰레드가 끝나면, 응답함,
 * -> 별도의 서블릿쓰레드를 할당받음
 * -> NIO가 요청할때 받은 커넥션을 계속가지고있으니, 해당 커넥션에 응답.
 * -> 따라서 적은 쓰레드풀로, 많은 커넥션이 가능
 */

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class SpringChapter4Application3 {

    @RequiredArgsConstructor
    @RestController
    public static class MyController {

        @GetMapping("/async")
        public String async() throws InterruptedException {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        }

        @GetMapping("/callable")
        public Callable<String> callable(){
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000); // 시간이 오래걸리는 작업.
                return "hello";
            };

            /**
             * 2020-06-12 10:05:23.580  INFO 10528 --- [nio-8080-exec-2] c.p.s.t.r.c.s.SpringChapter4Application3 : callable // 서블릿 쓰레드
             * 2020-06-12 10:05:23.589  INFO 10528 --- [         task-1] c.p.s.t.r.c.s.SpringChapter4Application3 : async // Spring 쓰레드
             */
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringChapter4Application3.class, args);
    }

}
