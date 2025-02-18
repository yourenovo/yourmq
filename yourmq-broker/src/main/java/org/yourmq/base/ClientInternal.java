package org.yourmq.base;



public interface ClientInternal extends Client {
    ClientConnectHandler getConnectHandler();

    ClientHeartbeatHandler getHeartbeatHandler();

    long getHeartbeatInterval();

    ClientConfig getConfig();

    Processor getProcessor();
}
