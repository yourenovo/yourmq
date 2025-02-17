//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.yourmq.common.Entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class MessageDefault implements MessageInternal {
    private final int flag;
    private final String sid;
    private final String event;
    private final Entity entity;

    public MessageDefault(int flag, String sid, String event, Entity entity) {
        this.flag = flag;
        this.sid = sid;
        this.event = event;
        this.entity = entity;
    }
@Override
    public int flag() {
        return this.flag;
    }
    @Override
    public boolean isEnd() {
        return this.flag == 49;
    }
    @Override
    public boolean isRequest() {
        return this.flag == 41;
    }
    @Override
    public boolean isSubscribe() {
        return this.flag == 42;
    }
    @Override
    public String sid() {
        return this.sid;
    }
    @Override
    public String event() {
        return this.event;
    }
    @Override
    public Entity entity() {
        return this.entity;
    }
    @Override
    public String toString() {
        return "Message{sid='" + this.sid + '\'' + ", event='" + this.event + '\'' + ", entity=" + this.entity + '}';
    }
    @Override
    public String metaString() {
        return this.entity.metaString();
    }
    @Override
    public Map<String, String> metaMap() {
        return this.entity.metaMap();
    }
    @Override
    public String meta(String name) {
        return this.entity.meta(name);
    }
    @Override
    public String metaOrDefault(String name, String def) {
        return this.entity.metaOrDefault(name, def);
    }
    @Override
    public void putMeta(String name, String val) {
        this.entity.putMeta(name, val);
    }
    @Override
    public void delMeta(String name) {
        this.entity.delMeta(name);
    }
    @Override
    public ByteBuffer data() {
        return this.entity.data();
    }
    @Override
    public String dataAsString() {
        return this.entity.dataAsString();
    }
    @Override
    public byte[] dataAsBytes() {
        return this.entity.dataAsBytes();
    }
    @Override
    public int dataSize() {
        return this.entity.dataSize();
    }
    @Override
    public void release() throws IOException {
        if (this.entity != null) {
            this.entity.release();
        }

    }
}
