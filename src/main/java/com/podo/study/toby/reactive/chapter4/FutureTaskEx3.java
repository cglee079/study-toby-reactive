package com.podo.study.toby.reactive.chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 콜백 구현
 */

@Slf4j
public class FutureTaskEx3 { //Callback

    interface SuccessCallback {
        void onSuccess(String result);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        private SuccessCallback successCallback;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback successCallback) {
            super(callable);
            this.successCallback = Objects.requireNonNull(successCallback);
        }

        @Override
        protected void done() {
            try {
                successCallback.onSuccess(get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final CallbackFutureTask callbackFutureTask = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";

        }, (res) -> log.debug("{}", res));

        final ExecutorService es = Executors.newSingleThreadExecutor();

        es.execute(callbackFutureTask);

        Thread.sleep(2100);
        log.debug("Exit");

    }
}

