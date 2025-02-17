package org.yourmq.client.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteSelectorDefault<T> implements RouteSelector<T> {
    private final Map<String, T> inner = new ConcurrentHashMap();

    public RouteSelectorDefault() {
    }
@Override
    public T select(String route) {
        return this.inner.get(route);
    }
    @Override
    public void put(String route, T target) {
        this.inner.put(route, target);
    }
    @Override
    public void remove(String route) {
        this.inner.remove(route);
    }
    @Override
    public int size() {
        return this.inner.size();
    }
}
