//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import org.yourmq.common.EntityDefault;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

public class CodecDefault implements Codec {
    private final Config config;

    public CodecDefault(Config config) {
        this.config = config;
    }

    @Override
    public <T extends CodecWriter> T write(Frame frame, Function<Integer, T> writerFactory) throws IOException {
        if (frame.message() == null) {
            int frameSize = 8;
            T writer = (T) writerFactory.apply(Integer.valueOf(frameSize));
            writer.putInt(frameSize);
            writer.putInt(frame.flag());
            writer.flush();
            return writer;
        } else {
            byte[] sidB = frame.message().sid().getBytes(this.config.getCharset());
            byte[] eventB = frame.message().event().getBytes(this.config.getCharset());
            byte[] metaStringB = frame.message().metaString().getBytes(this.config.getCharset());
            int frameSize = 8 + sidB.length + eventB.length + metaStringB.length + frame.message().dataSize() + 6;
            T writer = (T) writerFactory.apply(frameSize);
            writer.putInt(frameSize);
            writer.putInt(frame.flag());
            writer.putBytes(sidB);
            writer.putChar(10);
            writer.putBytes(eventB);
            writer.putChar(10);
            writer.putBytes(metaStringB);
            writer.putChar(10);
            writer.putBytes(frame.message().dataAsBytes());
            writer.flush();
            return writer;
        }
    }

    @Override
    public Frame read(CodecReader reader) {
        int frameSize = reader.getInt();
        if (frameSize > reader.remaining() + 4) {
            return null;
        } else if (frameSize > 17825792) {
            reader.skipBytes(frameSize - 4);
            return null;
        } else {
            int flag = reader.getInt();
            if (frameSize == 8) {
                return new Frame(Flags.of(flag), (MessageInternal) null);
            } else {
                int metaBufSize = Math.min(4096, reader.remaining());
                ByteBuffer buf = ByteBuffer.allocate(metaBufSize);
                String sid = this.decodeString(reader, buf, 64);
                String event = this.decodeString(reader, buf, 512);
                String metaString = this.decodeString(reader, buf, 4096);
                int dataRealSize = frameSize - reader.position();
                byte[] data;
                if (dataRealSize > 16777216) {
                    data = new byte[16777216];
                    reader.getBytes(data, 0, 16777216);

                    for (int i = dataRealSize - 16777216; i > 0; --i) {
                        reader.getByte();
                    }
                } else {
                    data = new byte[dataRealSize];
                    if (dataRealSize > 0) {
                        reader.getBytes(data, 0, dataRealSize);
                    }
                }

                MessageInternal message = (new MessageBuilder()).flag(Flags.of(flag)).sid(sid).event(event).entity((new EntityDefault()).dataSet(data).metaStringSet(metaString)).build();
                return new Frame(message.flag(), message);
            }
        }
    }

    protected String decodeString(CodecReader reader, ByteBuffer buf, int maxLen) {
        buf.clear();

        while (true) {
            byte c;
            do {
                c = reader.getByte();
                if (c == 0 && reader.peekByte() == 10) {
                    reader.skipBytes(1);
                    buf.flip();
                    if (buf.limit() < 1) {
                        return "";
                    }

                    return new String(buf.array(), 0, buf.limit(), this.config.getCharset());
                }
            } while (maxLen > 0 && maxLen <= buf.position());

            buf.put(c);
        }
    }
}
