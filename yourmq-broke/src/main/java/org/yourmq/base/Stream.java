package org.yourmq.base;

import java.util.function.Consumer;

public interface Stream<T extends Stream> {
    String sid();

    boolean isDone();

    T thenError(Consumer<Throwable> onError);

    T thenProgress(TriConsumer<Boolean, Integer, Integer> onProgress);
}