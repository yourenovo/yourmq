package org.yourmq.base;



public interface StreamInternal<T extends Stream> extends Stream<T> {
    int demands();

    long timeout();

    void insuranceStart(StreamManger streamManger, long streamTimeout);

    void insuranceCancel();

    void onReply(MessageInternal reply);

    void onError(Throwable error);

    void onProgress(boolean isSend, int val, int max);
}