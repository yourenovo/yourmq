package org.yourmq.base;

@FunctionalInterface
public interface ClientHeartbeatHandler {
    void clientHeartbeat(Session session) throws Exception;
}
