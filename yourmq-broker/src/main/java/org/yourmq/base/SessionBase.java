
package org.yourmq.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SessionBase implements Session {
    protected final Channel channel;
    private final String sessionId;
    private Map<String, Object> attrMap;

    public SessionBase(Channel channel) {
        this.channel = channel;
        this.sessionId = this.generateId();
    }

    @Override

    public Map<String, Object> attrMap() {
        if (this.attrMap == null) {
            this.attrMap = new ConcurrentHashMap();
        }

        return this.attrMap;
    }

    @Override
    public boolean attrHas(String name) {
        return this.attrMap == null ? false : this.attrMap.containsKey(name);
    }

    @Override
    public <T> T attr(String name) {
        return this.attrMap == null ? null : (T) this.attrMap.get(name);
    }

    @Override
    public <T> T attrOrDefault(String name, T def) {
        T tmp = this.attr(name);
        return tmp == null ? def : tmp;
    }

    @Override
    public <T> Session attrPut(String name, T value) {
        if (this.attrMap == null) {
            this.attrMap = new ConcurrentHashMap();
        }

        this.attrMap.put(name, value);
        return this;
    }

    @Override
    public <T> Session attrDel(String name) {
        if (this.attrMap != null) {
            this.attrMap.remove(name);
        }

        return this;
    }

    @Override

    public Config config() {
        return this.channel.getConfig();
    }

    @Override
    public String sessionId() {
        return this.sessionId;
    }

    @Override
    public boolean isActive() {
        return this.isValid() && !this.isClosing();
    }

    @Override
    public long liveTime() {
        return this.channel.getLiveTime();
    }

    @Override
    public int closeCode() {
        return this.channel.closeCode();
    }

    protected String generateId() {
        return this.channel.getConfig().genId();
    }
}
