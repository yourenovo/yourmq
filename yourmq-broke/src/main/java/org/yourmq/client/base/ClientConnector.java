package org.yourmq.client.base;



import java.io.IOException;

public interface ClientConnector {
    ClientConfig getConfig();

    boolean autoReconnect();

    ChannelInternal connect() throws IOException;

    void close() throws IOException;
}
