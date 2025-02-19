
package org.yourmq.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TcpNioChannelAssistant implements ChannelAssistant<Channel> {
    public TcpNioChannelAssistant() {
    }

    @Override
    public void write(Channel target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ChannelPromise writePromise = target.newPromise();
            writePromise.addListener((future) -> {
                if (future.isSuccess()) {
                    completionHandler.completed(true, (Throwable) null);
                } else {
                    completionHandler.completed(false, future.cause());
                }

            });
            target.writeAndFlush(frame, writePromise);
        } catch (Throwable var6) {
            completionHandler.completed(false, var6);
        }

    }

    @Override
    public boolean isValid(Channel target) {
        return target.isActive();
    }

    @Override
    public void close(Channel target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(Channel target) {
        return (InetSocketAddress) target.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(Channel target) {
        return (InetSocketAddress) target.localAddress();
    }
}
