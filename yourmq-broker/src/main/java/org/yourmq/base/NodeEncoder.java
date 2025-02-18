package org.yourmq.base;

public interface NodeEncoder<T> {
    void encode(T data, ONode node);
}
