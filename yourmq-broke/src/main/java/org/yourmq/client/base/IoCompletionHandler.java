package org.yourmq.client.base;

public interface IoCompletionHandler {
    void completed(boolean result, Throwable throwable);
}