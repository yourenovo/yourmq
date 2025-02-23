
package org.yourmq.base;

import org.yourmq.inter.ConfigBase;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientConfig extends ConfigBase<ClientConfig> {
    private final String schema;
    private final String schemaCleaned;
    private final String linkUrl;
    private final String url;
    private final String host;
    private final int port;
    private final Map<String, String> metaMap = new LinkedHashMap();
    private long heartbeatInterval;
    private long connectTimeout;
    private boolean autoReconnect;

    public ClientConfig(String url) {
        super(true);
        int idx = url.indexOf("://");
        if (idx < 2) {
            throw new IllegalArgumentException("The serverUrl invalid: " + url);
        } else {
            this.schema = url.substring(0, idx);
            if (url.startsWith("sd:")) {
                url = url.substring(3);
            }

            URI uri = URI.create(url);
            this.linkUrl = "sd:" + url;
            this.url = url;
            this.host = uri.getHost();
            this.port = uri.getPort() < 0 ? 8602 : uri.getPort();
            this.schemaCleaned = uri.getScheme();
            this.connectTimeout = 10000L;
            this.heartbeatInterval = 20000L;
            this.autoReconnect = true;
        }
    }

    public String getLinkUrl() {
        return this.linkUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Map<String, String> getMetaMap() {
        return this.metaMap;
    }

    public ClientConfig metaPut(String name, String val) {
        this.metaMap.put(name, val);
        return this;
    }

    public long getHeartbeatInterval() {
        return this.heartbeatInterval;
    }

    public ClientConfig heartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public long getConnectTimeout() {
        return this.connectTimeout;
    }

    public ClientConfig connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public ClientConfig autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }
@Override
    public ClientConfig idleTimeout(int idleTimeout) {
        return !this.autoReconnect ? (ClientConfig)super.idleTimeout(idleTimeout) : (ClientConfig)super.idleTimeout(0);
    }
@Override
    public String toString() {
        return "ClientConfig{schema='" + this.schemaCleaned + '\'' + ", charset=" + this.charset + ", url='" + this.url + '\'' + ", ioThreads=" + this.ioThreads + ", codecThreads=" + this.codecThreads + ", exchangeThreads=" + this.workThreads + ", heartbeatInterval=" + this.heartbeatInterval + ", connectTimeout=" + this.connectTimeout + ", idleTimeout=" + this.idleTimeout + ", requestTimeout=" + this.requestTimeout + ", streamTimeout=" + this.streamTimeout + ", readBufferSize=" + this.readBufferSize + ", writeBufferSize=" + this.writeBufferSize + ", autoReconnect=" + this.autoReconnect + ", maxUdpSize=" + this.maxUdpSize + '}';
    }

    @Override
    public boolean isNolockSend() {
        return false;
    }

    @Override
    public TrafficLimiter getTrafficLimiter() {
        return null;
    }
}
