package org.yourmq.client.base;

public interface RouteSelector<T> {
    T select(String route);

    void put(String route, T target);

    void remove(String route);

    int size();
}
