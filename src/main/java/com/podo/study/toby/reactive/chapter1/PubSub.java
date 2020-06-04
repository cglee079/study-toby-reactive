package com.podo.study.toby.reactive.chapter1;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Publisher === Observable
 * Subscriber === Observer
 * <p>
 * reactive streams 프로토콜은 다음을 규약한다.
 * <p>
 * subscribe()하면
 * <p>
 * 1. onSubscribe : 최초 1회 반드시 실행
 * 2. onNext : 0 ~ N 실행
 * 3. onError || onComplete : 종료 시 실행
 * <p>
 * 멀티스레드로 하면 동시성 문제가 발생하지않을까?
 * 예를 들어, sub가 순서가 꼬여서 데이터를 받는다던가
 * 따라서 onNext를 한 쓰레드에서만 호출하는것을 규약했다.
 */
@Slf4j
public class PubSub {

    public static void main(String[] args) {
        final Publisher<Integer> pub = new Publisher<Integer>() {

            final ExecutorService es = Executors.newSingleThreadExecutor();
            final Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(
                        // Subscription은 pub와 sub의 중재자.
                        // 예) pub가 빠름, sub가 느림 -> Subscription을 통해 sub가 조금 요청
                        new Subscription() {
                            @Override
                            public void request(long n) { // n 은 몇개의 데이터를 요청 할지
                                es.execute(() -> {
                                    try {
                                        for (Integer i : iter) {
                                            sub.onNext(i);
                                        }
                                        sub.onComplete();

                                    } catch (Exception e) {
                                        sub.onError(e);
                                    }
                                });
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        };

        final Subscriber<Integer> sub = new Subscriber<Integer>() {
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

        pub.subscribe(sub);

    }
}
