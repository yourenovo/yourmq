package org.yourmq.snap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventListenPipeline<Event> implements EventListener<Event> {
    private List<EH> list = new ArrayList();

    public EventListenPipeline() {
    }

    public void add(EventListener<Event> listener) {
        this.add(0, listener);
    }

    public void add(int index, EventListener<Event> listener) {
        this.list.add(new EH(index, listener));
        this.list.sort(Comparator.comparing(EH::getIndex));
    }

    public void remove(EventListener<?> listener) {
        for (int i = 0; i < this.list.size(); ++i) {
            if (listener.equals(((EH) this.list.get(i)).listener)) {
                this.list.remove(i);
                --i;
            }
        }

    }

    @Override
    public void onEvent(Event event) throws Throwable {
        for (int i = 0; i < this.list.size(); ++i) {
            ((EH) this.list.get(i)).listener.onEvent(event);
        }

    }

    static class EH<Event> {
        int index;
        EventListener<Event> listener;

        EH(int index, EventListener<Event> listener) {
            this.index = index;
            this.listener = listener;
        }

        public int getIndex() {
            return this.index;
        }
    }
}
