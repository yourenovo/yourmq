package org.yourmq.client.base;

public interface RunnableEx<Throw extends Throwable> {
    void run() throws Throw;
}