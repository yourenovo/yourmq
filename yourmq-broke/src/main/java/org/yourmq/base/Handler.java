package org.yourmq.base;

public interface Handler {
    void handle(Context context) throws Exception;
}