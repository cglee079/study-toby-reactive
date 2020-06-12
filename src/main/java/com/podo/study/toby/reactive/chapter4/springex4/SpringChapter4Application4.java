package com.podo.study.toby.reactive.chapter4.springex4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * DeferredResult 큐, Spring 3.2, 스프링 비동기 기술의 꽃!
 * 요청을 보냄
 * -> 시간이 걸리는 작업을 수행하지 않음
 * -> 큐에 대기
 * -> 어떤 이벤트 발생
 * -> 로직 수행 후, 응답.
 *
 * dr.setResult() or dr.setException() 이 발생하기 전까지는, 응답하지 않고 대기하고있음.
 * 결과를 셋팅해주는 순간 바로 응답함.
 *
 * 채팅방을 예로.
 * 채팅방에 누군가 커넥션을 유지하고, DeferredResult를 받는다.
 * 누군가 채팅을 보내서, 이벤트를 발생시키고, DeferredResult 커넥션을 유지한 사용자는 메세지를 받는다.
 * 그후 다시 요청하여 DeferredResult를 받아 커넥션을 유지한다.
 *
 * -> 가장 큰 특징은 워크쓰레드가 따로 만들어지지 않는다.
 * -> DeferredResult 오브젝트 만 메모리에 유지되면, 언제든지 DeferredResult를 불러와서 결과를 넣어주면
 * -> Spring MVC 결과를 리턴한 방식과, 동일한 방식으로 응답가능.
 * -> 서블릿 자원을 최소화 함으로써, 동작가능
 * -> 이벤트성 구조에 유용하다.
 *
 **/

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@SpringBootApplication
public class SpringChapter4Application4 {

    @RequiredArgsConstructor
    @RestController
    public static class MyController {
        private Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

        @GetMapping("/dr")
        public DeferredResult<String> dr(){
            log.info("dr");

            final DeferredResult<String> deferredResult = new DeferredResult<>(6000000L);

            results.add(deferredResult);

            return deferredResult;
        }

        @GetMapping("/dr/count")
        public String drCount() {
            return String.valueOf(results.size());
        }

        @GetMapping("/dr/event")
        public String drEvent(String message){
            for (DeferredResult<String> dr : results) {
                dr.setResult("Hello " + message);
                results.remove(dr);
            }

            return "OK";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringChapter4Application4.class, args);
    }

}
