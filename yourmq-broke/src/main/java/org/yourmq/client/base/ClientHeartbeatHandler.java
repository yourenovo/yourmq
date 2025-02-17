package org.yourmq.client.base;

@FunctionalInterface
public interface ClientHeartbeatHandler {
    void clientHeartbeat(Session session) throws Exception;
}
