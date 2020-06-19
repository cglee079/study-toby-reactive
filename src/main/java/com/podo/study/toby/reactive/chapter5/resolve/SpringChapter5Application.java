package com.podo.study.toby.reactive.chapter5.resolve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;


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
public class SpringChapter5Application {

    @RestController
    public static class MyController {

        private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

        @GetMapping("/rest")
        public ListenableFuture<ResponseEntity<String>> rest(String idx) {
            // 리모트 서버 호출, 리모트 서버에서 오래걸린다면?
            // -> CPU는 놀고있음..
            // -> 이문제를 해결해보자, 내가 외부에 호출할때는 쓰레드 자원을 낭비하고싶지 않아!
            // -> ListenableFuture 리턴하면, 스프링 MVC가 알아서 콜백 등록.
            // -> 오 2.x초걸림!
            // -> 그럼 어떻게 이게 가능한거지? 뚜껑을 열어보장.
            // -> JMC를 열어보니 백그라운드 쓰레드 100개 생성..
            // -> AsyncRestTemplate가 내부적으로 쓰레드 100개를 만들었음.
            // -> 아..?!

            final ListenableFuture<ResponseEntity<String>> response =
                    restTemplate.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);

            return response;

        }
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringChapter5Application.class, args);
    }
}
