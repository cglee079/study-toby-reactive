package com.podo.study.toby.reactive.chapter2;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.List;

// Reactive Streams - Operator
// Publisher -> Data -> Subscriber
// Publisher -> Data -> Oper1 -> Date2 -> Oper2 -> Data3 -> Subscriber

@Slf4j
public class PubSub {

    public static void main(String[] args) {
        final Publisher<Integer> pub = iterPub(Arrays.asList(1, 2, 3, 4, 5));
        final Subscriber<Integer> sub = logSub();

        pub.subscribe(sub);
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("on Subscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.debug("on Next : {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("on Error");
            }

            @Override
            public void onComplete() {
                log.debug("on Complete");
            }
        };
    }

    private static Publisher<Integer> iterPub(final List<Integer> iter) {
        return new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(
                        // Subscription은 pub와 sub의 중재자.
                        // 예) pub가 빠름, sub가 느림 -> Subscription을 통해 sub가 조금 요청
                        new Subscription() {
                            @Override
                            public void request(long n) { // n 은 몇개의 데이터를 요청 할지
                                try {
                                    for (Integer i : iter) {
                                        sub.onNext(i);
                                    }
                                    sub.onComplete();

                                } catch (Exception e) {
                                    sub.onError(e);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        };
    }
}
