//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.inter;

import io.netty.buffer.ByteBuf;
import org.yourmq.base.CodecWriter;

import java.io.IOException;

public class NettyBufferCodecWriter implements CodecWriter {
    private ByteBuf target;

    public NettyBufferCodecWriter(ByteBuf target) {
        this.target = target;
    }

    @Override
    public void putBytes(byte[] bytes) throws IOException {
        this.target.writeBytes(bytes);
    }

    @Override
    public void putInt(int val) throws IOException {
        this.target.writeInt(val);
    }

    @Override
    public void putChar(int val) throws IOException {
        this.target.writeChar(val);
    }

    @Override
    public void flush() throws IOException {
    }

    public ByteBuf getBuffer() {
        return this.target;
    }
}
