//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.YourMQ;
import org.yourmq.utils.StrUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ClusterClient implements Client {
    private final String[] serverUrls;
    private ClientConnectHandler connectHandler;
    private ClientHeartbeatHandler heartbeatHandler;
    private ClientConfigHandler configHandler;
    private Listener listener;

    public ClusterClient(String... serverUrls) {
        this.serverUrls = serverUrls;
    }
    @Override
    public Client connectHandler(ClientConnectHandler connectHandler) {
        this.connectHandler = connectHandler;
        return this;
    }
    @Override
    public Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
        return this;
    }
    @Override
    public Client config(ClientConfigHandler configHandler) {
        this.configHandler = configHandler;
        return this;
    }@Override

    public Client listen(Listener listener) {
        this.listener = listener;
        return this;
    }
    @Override
    public ClientSession open() {
        try {
            return this.openDo(false);
        } catch (IOException var2) {
            throw new IllegalStateException(var2);
        }
    }
    @Override
    public ClientSession openOrThow() throws IOException {
        return this.openDo(true);
    }

    private ClientSession openDo(boolean isThow) throws IOException {
        List<ClientSession> sessionList = new ArrayList();
        ExecutorService workExecutor = null;
        String[] var4 = this.serverUrls;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String urls = var4[var6];
            String[] var8 = urls.split(",");
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String url = var8[var10];
                url = url.trim();
                if (!StrUtils.isEmpty(url)) {
                    ClientInternal client = (ClientInternal) YourMQ.createClient(url);
                    if (this.listener != null) {
                        client.listen(this.listener);
                    }

                    if (this.configHandler != null) {
                        client.config(this.configHandler);
                    }

                    if (this.connectHandler != null) {
                        client.connectHandler(this.connectHandler);
                    }

                    if (this.heartbeatHandler != null) {
                        client.heartbeatHandler(this.heartbeatHandler);
                    }

                    if (workExecutor == null) {
                        workExecutor = client.getConfig().getWorkExecutor();
                    } else {
                        client.getConfig().workExecutor(workExecutor);
                    }

                    if (isThow) {
                        sessionList.add(client.openOrThow());
                    } else {
                        sessionList.add(client.open());
                    }
                }
            }
        }

        return new ClusterClientSession(sessionList);
    }
}
