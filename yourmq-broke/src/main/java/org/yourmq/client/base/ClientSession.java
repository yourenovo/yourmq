package org.yourmq.client.base;

import org.yourmq.common.Entity;

import java.io.Closeable;
import java.io.IOException;

public interface ClientSession extends Closeable {
    boolean isValid();

    boolean isActive();

    boolean isClosing();

    String sessionId();

    SendStream send(String event, Entity entity) throws IOException;

    default RequestStream sendAndRequest(String event, Entity entity) throws IOException {
        return this.sendAndRequest(event, entity, 0L);
    }

    RequestStream sendAndRequest(String event, Entity entity, long timeout) throws IOException;

    default SubscribeStream sendAndSubscribe(String event, Entity entity) throws IOException {
        return this.sendAndSubscribe(event, entity, 0L);
    }

    SubscribeStream sendAndSubscribe(String event, Entity entity, long timeout) throws IOException;

    /** @deprecated */
    @Deprecated
    default void closeStarting() throws IOException {
        this.preclose();
    }

    void preclose() throws IOException;

    void close() throws IOException;

    int closeCode();

    void reconnect() throws IOException;
}