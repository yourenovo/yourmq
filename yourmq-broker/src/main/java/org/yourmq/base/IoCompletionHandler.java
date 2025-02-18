package org.yourmq.base;

public interface IoCompletionHandler {
    void completed(boolean result, Throwable throwable);
}