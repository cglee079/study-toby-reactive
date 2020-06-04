package com.podo.study.toby.reactive.chapter3;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

    public static void main(String[] args) {
        Flux.range(1, 10)
                .log()
                .publishOn(Schedulers.newSingle("publishOn"))
                .subscribeOn(Schedulers.newSingle("subscribeOn"))
                .subscribe(System.out::println);
    }
}
