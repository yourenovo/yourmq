package org.yourmq.inter;

import org.yourmq.base.ChannelInternal;

public class ClientHandshakeResult {
    private final ChannelInternal channel;
    private final Throwable throwable;

    public ChannelInternal getChannel() {
        return this.channel;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public ClientHandshakeResult(ChannelInternal channel, Throwable throwable) {
        this.channel = channel;
        this.throwable = throwable;
    }
}