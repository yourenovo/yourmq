//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

import org.yourmq.base.ClientProvider;
import org.yourmq.base.ServerProvider;
import org.yourmq.common.YourSocket;

public class ProviderUtils {
    public ProviderUtils() {
    }

    public static void autoWeakLoad() {
        String[] _providers = new String[]{"org.noear.socketd.transport.netty.tcp.TcpNioProvider", "org.noear.socketd.transport.netty.udp.UdpNioProvider", "org.noear.socketd.transport.java_websocket.WsNioProvider", "org.noear.socketd.transport.smartsocket.tcp.TcpAioProvider", "org.noear.socketd.transport.java_kcp.KcpNioProvider", "org.noear.socketd.transport.java_tcp.TcpBioProvider", "org.noear.socketd.transport.java_udp.UdpBioProvider"};
        String[] var1 = _providers;
        int var2 = _providers.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String p1 = var1[var3];

            try {
                Class<?> clz = YourSocket.class.getClassLoader().loadClass(p1);
                if (clz != null) {
                    Object obj = clz.getDeclaredConstructor().newInstance();
                    if (obj instanceof ClientProvider) {
                        YourSocket.registerClientProvider((ClientProvider)obj);
                    }

                    if (obj instanceof ServerProvider) {
                        YourSocket.registerServerProvider((ServerProvider)obj);
                    }
                }
            } catch (Throwable var7) {
            }
        }

    }
}
