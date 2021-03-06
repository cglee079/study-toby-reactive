package com.podo.study.toby.reactive.chapter2;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

// Reactive Streams - Operator
// Publisher -> Data -> Subscriber
// Publisher -> Data -> Oper1 -> Date2 -> Oper2 -> Data3 -> Subscriber

@Slf4j
public class PubSub {

    public static void main(String[] args) {
        final Publisher<Integer> pub = iterPub(Arrays.asList(1, 2, 3, 4, 5));
        final Publisher<Integer> mapPub = mapPub(pub, (a) -> a * 10);
        final Publisher<String> reducePub = reducePub(mapPub, "", (a, b) -> a + "-" + b);
        final Subscriber<String> sub = logSub();

        reducePub.subscribe(sub);
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> func) {
        return new Publisher<R>() {
            R result = init;

            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    @Override
                    public void onNext(T i) {
                        result = func.apply(result, i);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

    private static <T> Publisher<T> mapPub(Publisher<T> pub, Function<T, T> func) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> sub) {
                pub.subscribe(new DelegateSub<T, T>(sub) {
                    @Override
                    public void onNext(T i) {
                        sub.onNext(func.apply(i));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("on Subscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
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

    private static <T> Publisher<T> iterPub(final List<T> iter) {
        return new Publisher<T>() {

            @Override
            public void subscribe(Subscriber<? super T> sub) {
                sub.onSubscribe(
                        // Subscription은 pub와 sub의 중재자.
                        // 예) pub가 빠름, sub가 느림 -> Subscription을 통해 sub가 조금 요청
                        new Subscription() {
                            @Override
                            public void request(long n) { // n 은 몇개의 데이터를 요청 할지
                                try {
                                    for (T i : iter) {
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
