package org.yourmq.base;

import org.yourmq.common.Message;

import java.io.IOException;

public interface Listener {
    void onOpen(Session session) throws IOException;

    void onMessage(Session session, Message message) throws IOException;

    default void onReply(Session session, Message message) {
    }

    default void onSend(Session session, Message message) {
    }

    void onClose(Session session);

    void onError(Session session, Throwable error);
}
