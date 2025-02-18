package org.yourmq.base;

public interface ServerProvider {
    String[] schemas();

    Server createServer(ServerConfig serverConfig);
}
