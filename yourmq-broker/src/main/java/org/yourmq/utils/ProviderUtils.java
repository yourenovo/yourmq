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
        String[] _providers = new String[]{"org.yourmq.inter.TcpNioProvider"};
        String[] var1 = _providers;
        int var2 = _providers.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String p1 = var1[var3];

            try {
                Class<?> clz = YourSocket.class.getClassLoader().loadClass(p1);
                if (clz != null) {
                    Object obj = clz.getDeclaredConstructor().newInstance();
                    if (obj instanceof ClientProvider) {
                        YourSocket.registerClientProvider((ClientProvider) obj);
                    }

                    if (obj instanceof ServerProvider) {
                        YourSocket.registerServerProvider((ServerProvider) obj);
                    }
                }
            } catch (Throwable var7) {
            }
        }

    }
}
