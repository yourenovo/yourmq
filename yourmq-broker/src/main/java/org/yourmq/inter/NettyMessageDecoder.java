package org.yourmq.inter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.yourmq.base.CodecReader;
import org.yourmq.base.Config;
import org.yourmq.base.Frame;

import java.util.List;

public class NettyMessageDecoder extends ByteToMessageDecoder {
    private final Config config;

    public NettyMessageDecoder(Config config) {
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        CodecReader reader = new NettyBufferCodecReader(inBuf);
        Frame message = this.config.getCodec().read(reader);
        if (message != null) {
            out.add(message);
        }
    }
}