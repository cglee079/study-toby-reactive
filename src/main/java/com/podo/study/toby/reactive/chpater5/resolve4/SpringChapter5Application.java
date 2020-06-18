package com.podo.study.toby.reactive.chpater5.resolve4;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;


/**
 * tomcat max thread 1개 실행, application.properties 설정 후 실행
 *
 *
 * Thread Pool Hell 발생
 * <p>
 * !) 그럼 쓰레드를 1000000만개로하면?
 * => 쓰레드는 메모리를 먹음
 * => 쓰레드가 코어를 넘어가면, 컨텍스트 스위치 발생 - cpu 소모
 * <p>
 * <p>
 * ! 새로운 프로그램 소개 -> JMC(java mission control), jdk/bin/jmc.exe // 상업용으로 사용하면 안됨, 개발용은 괜찮음.
 */

@SpringBootApplication
@EnableAsync
public class SpringChapter5Application {

    @RestController
    @RequiredArgsConstructor
    public static class MyController {

        private final MyService myService;
        private AsyncRestTemplate restTemplate = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @GetMapping("/rest")
        public DeferredResult<String> rest(String idx) {
            // 리모트 서버 호출, 리모트 서버에서 오래걸린다면?
            // -> CPU는 놀고있음..
            // -> 이문제를 해결해보자, 내가 외부에 호출할때는 쓰레드 자원을 낭비하고싶지 않아!
            // -> ListenableFuture 리턴하면, 스프링 MVC가 알아서 콜백 등록.
            // -> 오 2.x초걸림!
            // -> 그럼 어떻게 이게 가능한거지? 뚜껑을 열어보장.
            // -> JMC를 열어보니 백그라운드 쓰레드 100개 생성..
            // -> AsyncRestTemplate가 내부적으로 쓰레드 100개를 만들었음.
            // -> 아..?!
            // -> Netty를 이용해보자. 논블록킹 IO
            // -> 오! 쓰레드가 몇개 늘지 않는다.
            // -> QUIZ Netty의 기본 쓰레드 개수는 cpu 코어 개수 * 2
            // -> 로직을 추가해보자.
            // -> DeferredResult 를 사용.
            // -> 순차적으로 의존적인 API 호출을 해야한다면..?
            // -> 호출하고 로직을 수행해야한다면,, myService를 보자.
            // -> 질문) 메모리 사용량은 ? -> 쓰레드가 별로 없기 때문에, 쓰레드에 대한 메모리는 부담없음.

            final DeferredResult<String> dr = new DeferredResult<>();

            final ListenableFuture<ResponseEntity<String>> f1 =
                    restTemplate.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);

            f1.addCallback(s -> {
                        //s = ResponseEntity<String>
                        final ListenableFuture<ResponseEntity<String>> f2 =
                                restTemplate.getForEntity("http://localhost:8081/service2?req={req}", String.class, "hello" + idx);

                        f2.addCallback(s2 -> {
                                    final ListenableFuture<String> f3 = myService.work(s2.getBody());

                                    f3.addCallback(s3 -> {
                                        dr.setResult(s3);
                                    }, e3 -> {
                                        dr.setErrorResult(e3.getMessage());
                                    });

                                }, e2 -> {
                                    dr.setErrorResult(e2.getMessage());
                                }
                        );

                    }, e -> {
                        dr.setErrorResult(e.getMessage());
                    }
            );

            return dr;

        }
    }

    @Service
    public static class MyService {
        @Async
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/aysncwork");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPoll() {
        final ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();

        threadPoolExecutor.setCorePoolSize(1);
        threadPoolExecutor.setMaxPoolSize(1);
        threadPoolExecutor.initialize();

        return threadPoolExecutor;
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringChapter5Application.class, args);
    }
}
