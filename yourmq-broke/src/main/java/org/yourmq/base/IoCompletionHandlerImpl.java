package org.yourmq.base;

public class IoCompletionHandlerImpl implements IoCompletionHandler {
    private boolean result;
    private Throwable throwable;

    public IoCompletionHandlerImpl() {
    }

    public boolean getResult() {
        return this.result;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public void completed(boolean result, Throwable throwable) {
        this.result = result;
        this.throwable = throwable;
    }
}
