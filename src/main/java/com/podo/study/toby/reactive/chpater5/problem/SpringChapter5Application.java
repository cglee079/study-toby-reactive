package com.podo.study.toby.reactive.chpater5.problem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * tomcat max thread 1개 실행, application.properties 설정 후 실행
 *
 * LinkedIn에서 제공한 자료중,
 * 서버가 내부에 리모트 서버를 호출하는 구조를 보니 많은 요청에 Thread Pool Hell 발생
 * 써블릿 쓰레드가, 내부에 리모트 서버를 호출하는 동안 쓰레드를 차지하고있으니, 쓰레드가 부족하다..!
 *
 *
 * !) 그래, 그럼 쓰레드를 1000000만개로하면?
 * => 쓰레드는 메모리를 먹음
 * => 쓰레드가 코어를 넘어가면, 컨텍스트 스위치 발생 - cpu 소모
 *
 *
 * ! 새로운 프로그램 소개 -> JMC(java mission control), jdk/bin/jmc.exe // 상업용으로 사용하면 안됨, 개발용은 괜찮음.
 *
 */

@SpringBootApplication
public class SpringChapter5Application {

    @RestController
    public static class MyController{

        private RestTemplate restTemplate = new RestTemplate();

        @GetMapping("/rest")
        public String rest(String idx){
            // 리모트 서버 호출, 리모트 서버에서 오래걸린다면?
            // -> CPU는 놀고있음..
            // -> 이문제를 해결해보자, 내가 외부에 호출할때는 쓰레드 자원을 낭비하고싶지 않아!
            return restTemplate.getForObject("http://localhost:8081/service?req={req}", String.class,"hello" + idx);
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringChapter5Application.class, args);
    }
}
