
package org.yourmq.base;

public class ClientHeartbeatHandlerDefault implements ClientHeartbeatHandler {
    public ClientHeartbeatHandlerDefault() {
    }
@Override
    public void clientHeartbeat(Session session) throws Exception {
        session.sendPing();
    }
}
