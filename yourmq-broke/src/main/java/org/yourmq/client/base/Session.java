package org.yourmq.client.base;

import org.yourmq.common.Entity;
import org.yourmq.common.Message;
import org.yourmq.common.StringEntity;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public interface Session extends ClientSession, Closeable {
    InetSocketAddress remoteAddress() throws IOException;

    InetSocketAddress localAddress() throws IOException;

    Config config();

    Handshake handshake();

    default String name() {
        return this.param("@");
    }

    String param(String name);

    String paramOrDefault(String name, String def);

    String path();

    void pathNew(String pathNew);

    Map<String, Object> attrMap();

    boolean attrHas(String name);

    <T> T attr(String name);

    <T> T attrOrDefault(String name, T def);

    <T> Session attrPut(String name, T value);

    <T> Session attrDel(String name);

    @Override
    boolean isValid();

    @Override
    boolean isClosing();

    @Override
    String sessionId();

    long liveTime();

    @Override
    void reconnect() throws IOException;

    void sendPing() throws IOException;

    void sendAlarm(Message from, Entity alarm) throws IOException;

    /**
     * @deprecated
     */
    @Deprecated
    default void sendAlarm(Message from, String alarm) throws IOException {
        this.sendAlarm(from, (Entity) (new StringEntity(alarm)));
    }

    void sendPressure(Message from, Entity pressure) throws IOException;

    void reply(Message from, Entity entity) throws IOException;

    void replyEnd(Message from, Entity entity) throws IOException;
}