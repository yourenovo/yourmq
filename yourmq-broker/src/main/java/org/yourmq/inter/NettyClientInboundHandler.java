//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.inter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.yourmq.base.ChannelDefault;
import org.yourmq.base.ChannelInternal;
import org.yourmq.base.Frame;
import org.yourmq.base.TcpNioClient;
import org.yourmq.common.YourMQConnectionException;

import java.util.concurrent.CompletableFuture;

public class NettyClientInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");
    private final TcpNioClient client;
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture();

    public NettyClientInboundHandler(TcpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return this.handshakeFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ChannelInternal channel = new ChannelDefault(ctx.channel(), this.client);
        ctx.attr(CHANNEL_KEY).set(channel);
        channel.sendConnect(this.client.getConfig().getUrl(), this.client.getConfig().getMetaMap());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        ChannelInternal channel = (ChannelInternal) ctx.attr(CHANNEL_KEY).get();

        try {
            if (frame.flag() == 11) {
                channel.onOpenFuture((r, e) -> {
                    this.handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            this.client.getProcessor().reveFrame(channel, frame);
        } catch (YourMQConnectionException var5) {
            this.handshakeFuture.complete(new ClientHandshakeResult(channel, var5));
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        this.client.getProcessor().onClose(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        this.client.getProcessor().onError(channel, cause);
        ctx.close();
    }
}
