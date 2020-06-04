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
 * <p>
 * <p>
 * ## Quest.
 * 하나의 메인 쓰레드에서,
 * publisher, subscriber, subscription 이 동작함.
 * <p>
 * 실전에서는 쓸일이 필요 없음.
 * 옵저버를 쓸려면, 보통 외부에서 이벤트가 발생함.
 * 실전에서는, 이런 블록된 구조에서는 사용하지 않음.
 * <p>
 * ## Answer.
 * 리액티브에서 지원하는 Scheduler 를 이용해 해결하자.
 * <p>
 * 1. subscribeOn(스케줄러)
 * subscribe(), request(), onNext()..
 * 데이터를 쏴주는 작업을, 지정된 스케줄러 안에서 수행하라.
 * 메인 쓰레드를 블록킹하지않고, 다른 쓰레드에서, 데이터 생성.
 * -> publisher 가 굉장히 느린 경우, 사용해라.
 * <p>
 * 2. publishOn(스케줄러)
 * subscribe(), request()은 실행한 쪽에서 진행하고
 * onNext()와 같이 데이터를 받는 subscribes()를 별도의 쓰레드에서 처리
 * -> subscribe 가 굉장히 느린 경우, 사용해라.
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

//        TODO. shutdown
//        final Publisher<Integer> subscribeOnPub = sub -> {
//            ExecutorService es = Executors.newSingleThreadExecutor();
//            es.execute(() -> pub.subscribe(sub));
//        };

        final Publisher<Integer> publishOnPub = sub -> {
            pub.subscribe(new Subscriber<Integer>() {

                final ExecutorService es = Executors.newSingleThreadExecutor();

                @Override
                public void onSubscribe(Subscription subscription) {
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer i) {
                    es.execute(() -> sub.onNext(i));
                }

                @Override
                public void onError(Throwable t) {
                    es.execute(() -> sub.onError(t));
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                    es.shutdown();
                }
            });


        };

        publishOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("On Subscribe");
                subscription.request(1);
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

