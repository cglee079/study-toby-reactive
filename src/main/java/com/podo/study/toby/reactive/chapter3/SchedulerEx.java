package com.podo.study.toby.reactive.chapter3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 내부구조를 지금 까지 파악,
 * 내부구조를 파악하는것은 중요!
 * <p>
 * # Scheduler는 리액티브프로그래밍의 또 다른 하나의 축
 *
 *
 * Quest.
 * 하나의 메인 쓰레드에서,
 * publisher, subscriber, subscription 이 동작함.
 *
 * 실전에서는 쓸일이 필요 없음.
 * 옵저버를 쓸려면, 보통 외부에서 이벤트가 발생함.
 * 실전에서는, 이런 블록된 구조에서는 사용하지 않음.
 *
 * Answer.
 * 리액티브에서 지원하는 Scheduler 를 이용해 해결하자.
 *
 * 1. subscribeOn(스케줄러)
 * subscribe(), request(), onNext()..
 * 데이터를 쏴주는 작업을, 지정된 스케줄러 안에서 수행하라.
 * 메인 쓰레드를 블록킹하지않고, 다른 쓰레드에서, 데이터 생성.
 * -> publisher 가 굉장히 느린 경우, 사용해라.
 *
 */

@Slf4j
public class SchedulerEx {

    public static void main(String[] args) {
        final Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request : {}", n);

                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        final Publisher<Integer> subscribeOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> pub.subscribe(sub));
        };

        subscribeOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("On Subscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.debug("On Next : {}", i);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("On Error : ", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("On Complete");
            }
        });

        log.debug("EXIT");
    }


}

