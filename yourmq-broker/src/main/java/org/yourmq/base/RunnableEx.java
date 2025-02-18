package org.yourmq.base;

public interface RunnableEx<Throw extends Throwable> {
    void run() throws Throw;
}