package org.yourmq.snap;

public interface EventListener<Event> {
    void onEvent(Event event) throws Throwable;
}
