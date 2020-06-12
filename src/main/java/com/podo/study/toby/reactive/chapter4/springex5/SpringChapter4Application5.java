package com.podo.study.toby.reactive.chapter4.springex5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

/**
 * http 스펙중에, 한번 요청에 여러번 응답을 전송하는 스펙이있음.
 * <p>
 * 이때 사용하는게, ResponseBodyEmitter
 */

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class SpringChapter4Application5 {

    @RequiredArgsConstructor
    @RestController
    public static class MyController {
        private Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();


        @GetMapping("/emitter")
        public ResponseBodyEmitter emitter() {
            log.info("emitter");

            final ResponseBodyEmitter emitter = new ResponseBodyEmitter(); // Spring 4.2

            Executors.newSingleThreadExecutor().submit(() -> {

                try {
                    for (int i = 0; i < 50; i++) {
                        emitter.send("<p>String " + i + "</p>");
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                }
            });

            return emitter;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(SpringChapter4Application5.class, args);
    }

}
