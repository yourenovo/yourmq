//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.yourmq.utils.StrUtils;

public class ServerConfig extends ConfigBase<ServerConfig> {
    private final String schema;
    private final String schemaCleaned;
    private String host;
    private int port;

    public ServerConfig(String schema) {
        super(false);
        this.schema = schema;
        if (schema.startsWith("sd:")) {
            schema = schema.substring(3);
        }

        this.schemaCleaned = schema;
        this.host = "";
        this.port = 8602;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getHost() {
        return this.host;
    }

    public ServerConfig host(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public ServerConfig port(int port) {
        this.port = port;
        return this;
    }

    public String getLocalUrl() {
        return StrUtils.isEmpty(this.host) ? "sd:" + this.schemaCleaned + "://127.0.0.1:" + this.port : "sd:" + this.schemaCleaned + "://" + this.host + ":" + this.port;
    }
@Override
    public String toString() {
        return "ServerConfig{schema='" + this.schemaCleaned + '\'' + ", charset=" + this.charset + ", host='" + this.host + '\'' + ", port=" + this.port + ", ioThreads=" + this.ioThreads + ", codecThreads=" + this.codecThreads + ", exchangeThreads=" + this.workThreads + ", idleTimeout=" + this.idleTimeout + ", requestTimeout=" + this.requestTimeout + ", streamTimeout=" + this.streamTimeout + ", readBufferSize=" + this.readBufferSize + ", writeBufferSize=" + this.writeBufferSize + ", maxUdpSize=" + this.maxUdpSize + '}';
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
