package org.yourmq.base;

import java.io.IOException;

public interface CodecWriter {
    void putBytes(byte[] bytes) throws IOException;

    void putInt(int val) throws IOException;

    void putChar(int val) throws IOException;

    void flush() throws IOException;
}