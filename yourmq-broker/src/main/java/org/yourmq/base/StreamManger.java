package org.yourmq.base;

public interface StreamManger {
    void addStream(String sid, StreamInternal stream);

    StreamInternal getStream(String sid);

    void removeStream(String sid);
}