//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.yourmq.common.Entity;

public class MessageBuilder {
    private int flag = 0;
    private String sid = "";
    private String event = "";
    private Entity entity = null;

    public MessageBuilder() {
    }

    public MessageBuilder flag(int flag) {
        this.flag = flag;
        return this;
    }

    public MessageBuilder sid(String sid) {
        this.sid = sid;
        return this;
    }

    public MessageBuilder event(String event) {
        this.event = event;
        return this;
    }

    public MessageBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public MessageInternal build() {
        return new MessageDefault(this.flag, this.sid, this.event, this.entity);
    }
}
