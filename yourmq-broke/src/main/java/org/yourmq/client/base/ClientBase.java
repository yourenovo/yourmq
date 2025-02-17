//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.client.ProcessorDefault;
import org.yourmq.common.YourSocketException;

import java.io.IOException;

public abstract class ClientBase<T extends ChannelAssistant> implements ClientInternal {
    private static final Logger log = LoggerFactory.getLogger(ClientBase.class);
    protected Processor processor = new ProcessorDefault();
    protected ClientHeartbeatHandler heartbeatHandler;
    protected ClientConnectHandler connectHandler = new ClientConnectHandlerDefault();
    private final ClientConfig config;
    private final T assistant;

    public ClientBase(ClientConfig config, T assistant) {
        this.config = config;
        this.assistant = assistant;
    }

    public T getAssistant() {
        return this.assistant;
    }
@Override
    public ClientConfig getConfig() {
        return this.config;
    }
    @Override
    public Processor getProcessor() {
        return this.processor;
    }
    @Override
    public ClientConnectHandler getConnectHandler() {
        return this.connectHandler;
    }
    @Override
    public ClientHeartbeatHandler getHeartbeatHandler() {
        return this.heartbeatHandler;
    }
    @Override
    public long getHeartbeatInterval() {
        return this.config.getHeartbeatInterval();
    }
    @Override
    public Client connectHandler(ClientConnectHandler connectHandler) {
        if (connectHandler != null) {
            this.connectHandler = connectHandler;
        }

        return (Client) this;
    }
    @Override
    public Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler) {
        if (heartbeatHandler != null) {
            this.heartbeatHandler = heartbeatHandler;
        }

        return (Client) this;
    }
    @Override
    public Client config(ClientConfigHandler configHandler) {
        if (configHandler != null) {
            configHandler.clientConfig(this.config);
        }

        return (Client) this;
    }
    @Override
    public Client listen(Listener listener) {
        if (listener != null) {
            this.processor.setListener(listener);
        }

        return (Client) this;
    }
    @Override
    public ClientSession open() {
        try {
            return this.openDo(false);
        } catch (IOException var2) {
            throw new IllegalStateException(var2);
        }
    }
    @Override
    public ClientSession openOrThow() throws IOException {
        return this.openDo(true);
    }

    private Session openDo(boolean isThow) throws IOException {
        ClientConnector connector = this.createConnector();
        ClientChannel clientChannel = new ClientChannel(this, connector);

        try {
            clientChannel.connect();
            log.info("Socket.D client successfully connected: {link={}}", this.getConfig().getLinkUrl());
        } catch (Throwable var5) {
            if (isThow) {
                clientChannel.close(2008);
                if (!(var5 instanceof RuntimeException) && !(var5 instanceof IOException)) {
                    throw new YourSocketException("Socket.D client Connection failed", var5);
                }

                throw var5;
            }

            log.info("Socket.D client Connection failed: {link={}}", this.getConfig().getLinkUrl());
        }

        return clientChannel.getSession();
    }

    protected abstract ClientConnector createConnector();
}
