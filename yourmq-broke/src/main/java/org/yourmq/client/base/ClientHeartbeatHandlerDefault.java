//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

public class ClientHeartbeatHandlerDefault implements ClientHeartbeatHandler {
    public ClientHeartbeatHandlerDefault() {
    }
@Override
    public void clientHeartbeat(Session session) throws Exception {
        session.sendPing();
    }
}
