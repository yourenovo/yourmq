
package org.yourmq.utils;

import org.yourmq.snap.EventListenPipeline;
import org.yourmq.snap.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventBus {
    private static List<HH> sThrow = new ArrayList();
    private static List<HH> sOther = new ArrayList();
    private static Map<Class<?>, EventListenPipeline<?>> sPipeline = new HashMap();

    public EventBus() {
    }

    public static void publishAsync(Object event) {
        if (event != null) {
            RunUtils.async(() -> {
                try {
                    publish0(event);
                } catch (Throwable var2) {
                    publish(var2);
                }

            });
        }

    }

    public static void publishTry(Object event) {
        if (event != null) {
            try {
                publish0(event);
            } catch (Throwable var2) {
            }
        }

    }

    public static void publish(Object event) throws RuntimeException {
        if (event != null) {
            try {
                publish0(event);
            } catch (Throwable var2) {
                if (var2 instanceof RuntimeException) {
                    throw (RuntimeException) var2;
                }

            }
        }

    }

    private static void publish0(Object event) throws Throwable {
        if (event instanceof Throwable) {
            publish1(sThrow, event, false);
        } else {
            publish1(sOther, event, true);
        }

    }

    private static void publish1(List<HH> hhs, Object event, boolean thrown) throws Throwable {
        for (int i = 0; i < hhs.size(); ++i) {
            HH h1 = (HH) hhs.get(i);
            if (h1.t.isInstance(event)) {
                try {
                    h1.l.onEvent(event);
                } catch (Throwable var6) {
                    if (thrown) {
                        throw var6;
                    }

                    var6.printStackTrace();
                }
            }
        }

    }

    public static <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        Utils.locker().lock();

        try {
            pipelineDo(eventType).add(listener);
        } finally {
            Utils.locker().unlock();
        }

    }

    public static <T> void subscribe(Class<T> eventType, int index, EventListener<T> listener) {
        Utils.locker().lock();

        try {
            pipelineDo(eventType).add(index, listener);
        } finally {
            Utils.locker().unlock();
        }

    }

    private static <T> EventListenPipeline<T> pipelineDo(Class<T> eventType) {
        EventListenPipeline<T> pipeline = (EventListenPipeline) sPipeline.get(eventType);
        if (pipeline == null) {
            pipeline = new EventListenPipeline();
            sPipeline.put(eventType, pipeline);
            registerDo(eventType, pipeline);
        }

        return pipeline;
    }

    private static <T> void registerDo(Class<T> eventType, EventListener<T> listener) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            sThrow.add(new HH(eventType, listener));
        } else {
            sOther.add(new HH(eventType, listener));
        }

    }

    public static <T> void unsubscribe(EventListener<?> listener) {
        Utils.locker().lock();

        try {
            Class<?>[] ets = GenericUtil.resolveTypeArguments(listener.getClass(), EventListener.class);
            if (ets != null && ets.length > 0) {
                pipelineDo(ets[0]).remove(listener);
            }
        } finally {
            Utils.locker().unlock();
        }

    }

    static class HH {
        protected Class<?> t;
        protected EventListener l;

        public HH(Class<?> type, EventListener listener) {
            this.t = type;
            this.l = listener;
        }
    }
}
