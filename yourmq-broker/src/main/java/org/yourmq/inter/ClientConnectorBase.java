package org.yourmq.inter;

import org.yourmq.base.ClientConfig;
import org.yourmq.base.ClientConnector;
import org.yourmq.base.ClientInternal;

public abstract class ClientConnectorBase<T extends ClientInternal> implements ClientConnector {
    protected final T client;

    public ClientConnectorBase(T client) {
        this.client = client;
    }

    @Override
    public ClientConfig getConfig() {
        return this.client.getConfig();
    }

    @Override
    public boolean autoReconnect() {
        return this.client.getConfig().isAutoReconnect();
    }
}