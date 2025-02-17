//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.common.Entity;
import org.yourmq.common.EntityDefault;
import org.yourmq.common.Message;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SessionDefault extends SessionBase {
    private static final Logger log = LoggerFactory.getLogger(SessionDefault.class);
    private String pathNew;

    public SessionDefault(Channel channel) {
        super(channel);
    }

    @Override
    public boolean isValid() {
        return this.channel.isValid();
    }

    @Override
    public boolean isClosing() {
        return this.channel.isClosing();
    }

    @Override
    public InetSocketAddress remoteAddress() throws IOException {
        return this.channel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() throws IOException {
        return this.channel.getLocalAddress();
    }

    @Override
    public Handshake handshake() {
        return this.channel.getHandshake();
    }

    @Override
    public String param(String name) {
        return this.handshake().param(name);
    }

    @Override
    public String paramOrDefault(String name, String def) {
        return this.handshake().paramOrDefault(name, def);
    }

    @Override
    public String path() {
        return this.pathNew == null ? this.handshake().path() : this.pathNew;
    }

    @Override
    public void pathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    @Override
    public void reconnect() throws IOException {
        this.channel.reconnect();
    }

    @Override
    public void sendPing() throws IOException {
        this.channel.sendPing();
    }

    @Override
    public void sendAlarm(Message from, Entity alarm) throws IOException {
        this.channel.sendAlarm(from, alarm);
    }

    @Override
    public void sendPressure(Message from, Entity pressure) throws IOException {
        this.channel.sendPressure(from, pressure);
    }

    public SendStream send(String event, Entity entity) throws IOException {
        if (entity == null) {
            entity = new EntityDefault();
        }

        MessageInternal message = (new MessageBuilder()).sid(this.generateId()).event(event).entity((Entity) entity).build();
        SendStreamImpl stream = new SendStreamImpl(message.sid());
        this.channel.send(new Frame(40, message), stream);
        return stream;
    }

    public RequestStream sendAndRequest(String event, Entity entity, long timeout) throws IOException {
        if (entity == null) {
            entity = new EntityDefault();
        }

        MessageInternal message = (new MessageBuilder()).sid(this.generateId()).event(event).entity((Entity) entity).build();
        if (timeout < 0L) {
            timeout = this.channel.getConfig().getStreamTimeout();
        }

        if (timeout == 0L) {
            timeout = this.channel.getConfig().getRequestTimeout();
        }

        RequestStreamImpl stream = new RequestStreamImpl(message.sid(), timeout);
        this.channel.send(new Frame(41, message), stream);
        return stream;
    }

    public SubscribeStream sendAndSubscribe(String event, Entity entity, long timeout) throws IOException {
        if (entity == null) {
            entity = new EntityDefault();
        }

        MessageInternal message = (new MessageBuilder()).sid(this.generateId()).event(event).entity((Entity) entity).build();
        if (timeout <= 0L) {
            timeout = this.channel.getConfig().getStreamTimeout();
        }

        SubscribeStreamImpl stream = new SubscribeStreamImpl(message.sid(), timeout);
        this.channel.send(new Frame(42, message), stream);
        return stream;
    }

    @Override
    public void reply(Message from, Entity entity) throws IOException {
        if (entity == null) {
            entity = new EntityDefault();
        }

        MessageInternal message = (new MessageBuilder()).sid(from.sid()).event(from.event()).entity((Entity) entity).build();
        this.channel.send(new Frame(48, message), (StreamInternal) null);
    }

    @Override
    public void replyEnd(Message from, Entity entity) throws IOException {
        if (entity == null) {
            entity = new EntityDefault();
        }

        MessageInternal message = (new MessageBuilder()).sid(from.sid()).event(from.event()).entity((Entity) entity).build();
        this.channel.send(new Frame(49, message), (StreamInternal) null);
    }

    @Override
    public void preclose() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("{} session close starting, sessionId={}", this.channel.getConfig().getRoleName(), this.sessionId());
        }

        if (this.channel.isValid()) {
            this.channel.sendClose(1000);
        }

    }

    @Override
    public void close() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("{} session will be closed, sessionId={}", this.channel.getConfig().getRoleName(), this.sessionId());
        }

        if (this.channel.isValid()) {
            try {
                this.channel.sendClose(1001);
            } catch (Exception var2) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel sendClose error", this.channel.getConfig().getRoleName(), var2);
                }
            }
        }

        this.channel.close(2009);
    }
}
