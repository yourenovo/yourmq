package org.yourmq.base;

import java.lang.reflect.Type;

public interface NodeDecoder<T> {
    T decode(ONode node, Type type);
}