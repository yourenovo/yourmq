package org.yourmq.base;

public interface CodecReader {
    byte getByte();

    void getBytes(byte[] dst, int offset, int length);

    int getInt();

    byte peekByte();

    void skipBytes(int length);

    int remaining();

    int position();
}