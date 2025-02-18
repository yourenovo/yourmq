package org.yourmq.base;

import org.yourmq.common.Message;

import java.io.IOException;

@FunctionalInterface
public interface MessageHandler {
    void handle(Session session, Message message) throws IOException;
}
