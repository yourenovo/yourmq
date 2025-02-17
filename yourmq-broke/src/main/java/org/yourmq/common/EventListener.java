//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.common;

import org.yourmq.client.base.*;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventListener {
    private IoConsumer<Session> doOnOpenHandler;
    private MessageHandler doOnMessageHandler;
    private BiConsumer<Session, Message> doOnReplyHandler;
    private BiConsumer<Session, Message> doOnSendHandler;
    private Consumer<Session> doOnCloseHandler;
    private BiConsumer<Session, Throwable> doOnErrorHandler;
    private final RouteSelector<MessageHandler> eventRouteSelector;

    public EventListener() {
        this.eventRouteSelector = new RouteSelectorDefault();
    }

    public EventListener(RouteSelector<MessageHandler> routeSelector) {
        this.eventRouteSelector = routeSelector;
    }

    public EventListener doOnOpen(IoConsumer<Session> onOpen) {
        this.doOnOpenHandler = onOpen;
        return this;
    }

    public EventListener doOnMessage(MessageHandler onMessage) {
        this.doOnMessageHandler = onMessage;
        return this;
    }

    public EventListener doOnClose(Consumer<Session> onClose) {
        this.doOnCloseHandler = onClose;
        return this;
    }

    public EventListener doOnError(BiConsumer<Session, Throwable> onError) {
        this.doOnErrorHandler = onError;
        return this;
    }

    public EventListener doOn(String event, MessageHandler handler) {
        this.eventRouteSelector.put(event, handler);
        return this;
    }

    public void onOpen(Session session) throws IOException {
        if (this.doOnOpenHandler != null) {
            this.doOnOpenHandler.accept(session);
        }

    }

    public void onMessage(Session session, Message message) throws IOException {
        if (this.doOnMessageHandler != null) {
            this.doOnMessageHandler.handle(session, message);
        }

        MessageHandler messageHandler = (MessageHandler) this.eventRouteSelector.select(message.event());
        if (messageHandler != null) {
            messageHandler.handle(session, message);
        }

    }

    public void onReply(Session session, Message message) {
        if (this.doOnReplyHandler != null) {
            this.doOnReplyHandler.accept(session, message);
        }

    }

    public void onSend(Session session, Message message) {
        if (this.doOnSendHandler != null) {
            this.doOnSendHandler.accept(session, message);
        }

    }

    public void onClose(Session session) {
        if (this.doOnCloseHandler != null) {
            this.doOnCloseHandler.accept(session);
        }

    }

    public void onError(Session session, Throwable error) {
        if (this.doOnErrorHandler != null) {
            this.doOnErrorHandler.accept(session, error);
        }

    }
}
