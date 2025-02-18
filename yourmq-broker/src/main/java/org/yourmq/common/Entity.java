package org.yourmq.common;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public interface Entity {
    static StringEntity of(String data) {
        return new StringEntity(data);
    }

    static FileEntity of(File data) throws IOException {
        return new FileEntity(data);
    }

    static EntityDefault of(byte[] data) {
        return (new EntityDefault()).dataSet(data);
    }

    static EntityDefault of(ByteBuffer data) {
        return (new EntityDefault()).dataSet(data);
    }

    static EntityDefault of() {
        return new EntityDefault();
    }

    String metaString();

    Map<String, String> metaMap();

    String meta(String name);

    String metaOrDefault(String name, String def);

    default int metaAsInt(String name) {
        return Integer.parseInt(this.metaOrDefault(name, "0"));
    }

    default long metaAsLong(String name) {
        return Long.parseLong(this.metaOrDefault(name, "0"));
    }

    default float metaAsFloat(String name) {
        return Float.parseFloat(this.metaOrDefault(name, "0"));
    }

    default double metaAsDouble(String name) {
        return Double.parseDouble(this.metaOrDefault(name, "0"));
    }

    void putMeta(String name, String val);

    void delMeta(String name);

    ByteBuffer data();

    String dataAsString();

    byte[] dataAsBytes();

    int dataSize();

    void release() throws IOException;
}