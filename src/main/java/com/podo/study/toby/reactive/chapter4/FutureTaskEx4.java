package com.podo.study.toby.reactive.chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 콜백 구현 - 예외처리 포함.
 *
 * - Future : 블로킹방식으로, get
 * - Callback :
 * - 더나은방법..?!
 */

@Slf4j
public class FutureTaskEx4 { //Callback

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        private SuccessCallback successCallback;
        private ExceptionCallback exceptionCallback;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback successCallback, ExceptionCallback exceptionCallback) {
            super(callable);
            this.successCallback = Objects.requireNonNull(successCallback);
            this.exceptionCallback = Objects.requireNonNull(exceptionCallback);
        }

        @Override
        protected void done() {
            try {
                successCallback.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                exceptionCallback.onError(e.getCause());
            }
        }
    }

    // 비즈니스로직과 불필요한 로직이 혼재되있음.
    // 추상화를 통해서 분리,
    // 스프링이 이를 어떻게 처리하는지를 살펴보자.
    public static void main(String[] args) throws InterruptedException {
        final CallbackFutureTask callbackFutureTask = new CallbackFutureTask(() -> {
            //비즈니스 로직
            Thread.sleep(2000);
            log.info("Async");
            if (1 == 1) {
                throw new RuntimeException("Async Error !!");
            }
            return "Hello";

        }, res -> log.debug("{}", res), e -> log.error("", e));

        final ExecutorService es = Executors.newSingleThreadExecutor();

        es.execute(callbackFutureTask);

        Thread.sleep(2100);
        log.debug("Exit");

    }
}

