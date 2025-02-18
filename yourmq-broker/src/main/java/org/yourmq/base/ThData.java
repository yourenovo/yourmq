package org.yourmq.base;

import java.util.function.Supplier;

public class ThData<T> extends ThreadLocal<T> {
    private Supplier<T> _def;

    public ThData(Supplier<T> def) {
        this._def = def;
    }

    @Override
    protected T initialValue() {
        return this._def.get();
    }

    public static void clear() {
        JsonPath.clear();
        JsonFromer.clear();
        JsonToer.clear();
    }
}