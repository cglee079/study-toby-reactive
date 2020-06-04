package com.podo.study.toby.reactive.chapter1;


import lombok.extern.slf4j.Slf4j;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Duality(쌍대성) : 기능은 동일하나, 반대
 * <p>
 * Iterable <--> Observable 은 Duality(쌍대성)이다.
 * 기능은 동일하나,
 * Iterable은 pull 방식,
 * Observable은 push 방식
 *
 * 하지만, Observer 패턴은 문제가 있다.
 *
 * - 1. Complete 라는 개념이 없음
 * - 2. Error 처리 방식이 없음
 *
 * 따라서, 이두가지를 보완하여, 확장된 Observer Pattern을 구현
 * ReactiveX(RxJava) vs Reactive Streams (구현체 Reactor) - 리액티브 프로그래밍의 두 가지
 *
 * ## 확장된 Observer Pattern은 리액티브 프로그래밍의 한 축.
 */
@Slf4j
public class ObserverEx {

    private static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }

    public static void main(String[] args) {

        final IntObservable intObservable = new IntObservable();

        final Observer observer = new Observer() {

            @Override
            public void update(Observable o, Object arg) {
                log.debug(Thread.currentThread().getName() + " " + arg);
            }
        };

        intObservable.addObserver(observer);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(intObservable);
    }
}
