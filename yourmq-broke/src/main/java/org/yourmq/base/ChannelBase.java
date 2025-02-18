//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.common.Entity;
import org.yourmq.common.Message;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ChannelBase implements Channel {
    private final Config config;
    private final Map<String, Object> attachments = new ConcurrentHashMap();
    private HandshakeInternal handshake;

    public ChannelBase(Config config) {
        this.config = config;
    }
@Override
    public Config getConfig() {
        return this.config;
    }
    @Override

    public <T> T getAttachment(String name) {
        return (T) this.attachments.get(name);
    }
    @Override

    public void putAttachment(String name, Object val) {
        if (val == null) {
            this.attachments.remove(name);
        } else {
            this.attachments.put(name, val);
        }

    }
    @Override

    public void setHandshake(HandshakeInternal handshake) {
        if (handshake != null) {
            this.handshake = handshake;
        }

    }
    @Override

    public HandshakeInternal getHandshake() {
        return this.handshake;
    }
    @Override

    public void sendConnect(String uri, Map<String, String> metaMap) throws IOException {
        this.send(Frames.connectFrame(this.getConfig().genId(), uri, metaMap), (StreamInternal)null);
    }
    @Override

    public void sendConnack() throws IOException {
        this.send(Frames.connackFrame(this.getHandshake()), (StreamInternal)null);
    }
@Override
    public void sendPing() throws IOException {
        this.send(Frames.pingFrame(), (StreamInternal)null);
    }
    @Override
    public void sendPong() throws IOException {
        this.send(Frames.pongFrame(), (StreamInternal)null);
    }
    @Override
    public void sendClose(int code) throws IOException {
        this.send(Frames.closeFrame(code), (StreamInternal)null);
    }
    @Override
    public void sendAlarm(Message from, Entity alarm) throws IOException {
        this.send(Frames.alarmFrame(from, alarm), (StreamInternal)null);
    }
    @Override
    public void sendPressure(Message from, Entity pressure) throws IOException {
        this.send(Frames.pressureFrame(from, pressure), (StreamInternal)null);
    }
    @Override
    public void close(int code) {
        if (code > 1000) {
            this.attachments.clear();
        }

    }
}
