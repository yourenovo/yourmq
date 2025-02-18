package org.yourmq.base;

import org.yourmq.common.Entity;
import org.yourmq.exception.YourSocketException;

import java.io.IOException;

public interface BroadcastBroker {
    void broadcast(String event, Entity entity) throws IOException, YourSocketException;
}