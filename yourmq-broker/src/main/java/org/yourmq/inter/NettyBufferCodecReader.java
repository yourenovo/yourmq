package org.yourmq.inter;

import io.netty.buffer.ByteBuf;
import org.yourmq.base.CodecReader;

public class NettyBufferCodecReader implements CodecReader {
    private ByteBuf source;

    public NettyBufferCodecReader(ByteBuf source) {
        this.source = source;
    }

    @Override
    public byte getByte() {
        return this.source.readByte();
    }

    @Override
    public void getBytes(byte[] dst, int offset, int length) {
        this.source.readBytes(dst, offset, length);
    }

    @Override
    public int getInt() {
        return this.source.readInt();
    }

    @Override
    public byte peekByte() {
        return this.source.readableBytes() > 0 ? this.source.getByte(this.source.readerIndex()) : -1;
    }

    @Override
    public void skipBytes(int length) {
        this.source.skipBytes(length);
    }

    @Override
    public int remaining() {
        return this.source.readableBytes();
    }

    @Override
    public int position() {
        return this.source.readerIndex();
    }
}
