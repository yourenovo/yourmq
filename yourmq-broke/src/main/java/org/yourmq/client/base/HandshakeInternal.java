package org.yourmq.client.base;

import java.util.Map;

public interface HandshakeInternal extends Handshake {
    MessageInternal getSource();

    Map<String, String> getOutMetaMap();
}
