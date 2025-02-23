
package org.yourmq.common;

import org.yourmq.base.*;
import org.yourmq.inter.ServerConfig;
import org.yourmq.utils.ProviderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class YourSocket {
    private static Map<String, ClientProvider> clientProviderMap = new HashMap();
    private static Map<String, ServerProvider> serverProviderMap = new HashMap();

    public YourSocket() {
    }

    public static String version() {
        return "2.5.13";
    }

    public static String protocolName() {
        return "YourSocket";
    }

    public static String protocolVersion() {
        return "1.0";
    }

    public static void registerClientProvider(ClientProvider clientProvider) {
        String[] var1 = clientProvider.schemas();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String s = var1[var3];
            clientProviderMap.put(s, clientProvider);
        }

    }

    public static void registerServerProvider(ServerProvider serverProvider) {
        String[] var1 = serverProvider.schemas();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String s = var1[var3];
            serverProviderMap.put(s, serverProvider);
        }

    }

    public static Server createServer(String schema) {
        Server server = createServerOrNull(schema);
        if (server == null) {
            throw new IllegalStateException("No YourMQ server providers were found: " + schema);
        } else {
            return server;
        }
    }

    public static Server createServerOrNull(String schema) {
        ServerProvider factory = serverProviderMap.get(schema);
        return factory == null ? null : factory.createServer(new ServerConfig(schema));
    }

    public static Client createClient(String serverUrl) {
        Client client = createClientOrNull(serverUrl);
        if (client == null) {
            throw new IllegalStateException("No YourMQ client providers were found: " + serverUrl);
        } else {
            return client;
        }
    }

    public static Client createClientOrNull(String serverUrl) {
        int idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new IllegalArgumentException("The serverUrl invalid: " + serverUrl);
        } else {
            String schema = serverUrl.substring(0, idx);
            ClientProvider factory = clientProviderMap.get(schema);
            if (factory == null) {
                return null;
            } else {
                ClientConfig clientConfig = new ClientConfig(serverUrl);
                return factory.createClient(clientConfig);
            }
        }
    }

    public static ClusterClient createClusterClient(String... serverUrls) {
        return new ClusterClient(serverUrls);
    }

    public static ClusterClient createClusterClient(List<String> serverUrls) {
        return new ClusterClient((String[])serverUrls.toArray(new String[serverUrls.size()]));
    }

    static {
        ServiceLoader.load(ClientProvider.class).iterator().forEachRemaining((clientProvider) -> {
            registerClientProvider(clientProvider);
        });
        ServiceLoader.load(ServerProvider.class).iterator().forEachRemaining((serverProvider) -> {
            registerServerProvider(serverProvider);
        });
        ProviderUtils.autoWeakLoad();
    }
}
