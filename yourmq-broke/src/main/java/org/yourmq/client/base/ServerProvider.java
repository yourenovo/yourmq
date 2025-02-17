package org.yourmq.client.base;

public interface ServerProvider {
    String[] schemas();

    Server createServer(ServerConfig serverConfig);
}
