//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

public class NodeEncoderEntity<T> implements NodeEncoder<T> {
    private final Class<T> type;
    private final NodeEncoder<T> encoder;

    public NodeEncoderEntity(Class<T> type, NodeEncoder<T> encoder) {
        this.type = type;
        this.encoder = encoder;
    }

    public boolean isEncodable(Class<?> cls) {
        return this.type.isAssignableFrom(cls);
    }

    @Override
    public void encode(T source, ONode target) {
        this.encoder.encode(source, target);
    }
}
