package com.podo.study.toby.reactive.chapter2;

import reactor.core.publisher.Flux;

/**
 *
 * Reactor의 Flux를 log()를 보면,
 * PubSub의 operator를 구현한것과 같은, 동일한 로직을 확인 할 수 있다.
 *
 */
public class ReactorEx {

    public static void main(String[] args) {
        // Flux == publisher
        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.next(4);
            e.complete();
        })
                .log()
                .map(s -> s * 10)
                .log()
                .reduce(0, Integer::sum)
                .log()
                .subscribe(System.out::println);

    }
}
