
package org.yourmq.base;

import java.lang.reflect.Type;

public class NodeDecoderEntity<T> implements NodeDecoder<T> {
    private final Class<T> type;
    private final NodeDecoder<T> decoder;

    public NodeDecoderEntity(Class<T> type, NodeDecoder<T> decoder) {
        this.type = type;
        this.decoder = decoder;
    }

    public boolean isDecodable(Class<?> cls) {
        return this.type.isAssignableFrom(cls);
    }

    @Override
    public T decode(ONode source, Type type) {
        return this.decoder.decode(source, type);
    }
}
