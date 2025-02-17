package org.yourmq.client.base;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface ChannelAssistant<T> {
    void write(T target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler);

    boolean isValid(T target);

    void close(T target) throws IOException;

    InetSocketAddress getRemoteAddress(T target) throws IOException;

    InetSocketAddress getLocalAddress(T target) throws IOException;
}