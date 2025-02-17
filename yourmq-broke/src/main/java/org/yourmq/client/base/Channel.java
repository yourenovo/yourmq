package org.yourmq.client.base;

import org.yourmq.common.Entity;
import org.yourmq.common.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public interface Channel {
    <T> T getAttachment(String name);

    void putAttachment(String name, Object val);

    boolean isValid();

    boolean isClosing();

    int closeCode();

    void close(int code);

    long getLiveTime();

    Config getConfig();

    void setHandshake(HandshakeInternal handshake);

    HandshakeInternal getHandshake();

    InetSocketAddress getRemoteAddress() throws IOException;

    InetSocketAddress getLocalAddress() throws IOException;

    void sendConnect(String url, Map<String, String> metaMap) throws IOException;

    void sendConnack() throws IOException;

    void sendPing() throws IOException;

    void sendPong() throws IOException;

    void sendClose(int code) throws IOException;

    void sendAlarm(Message from, Entity alarm) throws IOException;

    void sendPressure(Message form, Entity pressure) throws IOException;

    void send(Frame frame, StreamInternal stream) throws IOException;

    void reconnect() throws IOException;

    void onError(Throwable error);

    Session getSession();

    /** @deprecated */
    @Deprecated
    default void writeAcquire(Frame frame) {
    }

    /** @deprecated */
    @Deprecated
    default void writeRelease(Frame frame) {
    }
}