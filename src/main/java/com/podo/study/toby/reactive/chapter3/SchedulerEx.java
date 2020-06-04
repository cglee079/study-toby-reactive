package com.podo.study.toby.reactive.chapter3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 내부구조를 지금 까지 파악,
 * 내부구조를 파악하는것은 중요!
 * <p>
 * # Scheduler는 리액티브프로그래밍의 또 다른 하나의 축
 */

@Slf4j
public class SchedulerEx {

    public static void main(String[] args) {
        final Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();;
                }

                @Override
                public void cancel() {

                }
            });
        };

        pub.subscribe(new Subscriber<Integer>() {
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
    }
}
