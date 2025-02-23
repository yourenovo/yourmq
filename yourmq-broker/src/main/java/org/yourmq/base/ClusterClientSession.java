
package org.yourmq.base;

import org.yourmq.common.Entity;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.RunUtils;
import org.yourmq.utils.StrUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ClusterClientSession implements ClientSession {
    private final List<ClientSession> sessionList;
    private final String sessionId;

    public ClusterClientSession(List<ClientSession> sessions) {
        this.sessionList = sessions;
        this.sessionId = StrUtils.guid();
    }

    public List<ClientSession> getSessionAll() {
        return Collections.unmodifiableList(this.sessionList);
    }

    public ClientSession getSessionAny(String diversionOrNull) {
        ClientSession session = null;
        if (StrUtils.isEmpty(diversionOrNull)) {
            session = LoadBalancer.getAnyByPoll(this.sessionList);
        } else {
            session = LoadBalancer.getAnyByHash(this.sessionList, diversionOrNull);
        }

        if (session == null) {
            throw new YourSocketException("No session is available!");
        } else {
            return session;
        }
    }

    /** @deprecated */
    @Deprecated
    public ClientSession getSessionOne() {
        return this.getSessionAny((String)null);
    }
    @Override
    public boolean isValid() {
        Iterator var1 = this.sessionList.iterator();

        ClientSession session;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            session = (ClientSession)var1.next();
        } while(!session.isValid());

        return true;
    }
    @Override
    public boolean isActive() {
        Iterator var1 = this.sessionList.iterator();

        ClientSession session;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            session = (ClientSession)var1.next();
        } while(!session.isActive());

        return true;
    }
    @Override
    public boolean isClosing() {
        Iterator var1 = this.sessionList.iterator();

        ClientSession session;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            session = (ClientSession)var1.next();
        } while(!session.isClosing());

        return true;
    }
    @Override
    public int closeCode() {
        Iterator var1 = this.sessionList.iterator();
        if (var1.hasNext()) {
            ClientSession session = (ClientSession)var1.next();
            return session.closeCode();
        } else {
            return 0;
        }
    }
@Override
    public String sessionId() {
        return this.sessionId;
    }
    @Override
    public SendStream send(String event, Entity entity) throws IOException {
        ClientSession sender = this.getSessionAny((String)null);
        return sender.send(event, entity);
    }
    @Override
    public RequestStream sendAndRequest(String event, Entity entity, long timeout) throws IOException {
        ClientSession sender = this.getSessionAny((String)null);
        return sender.sendAndRequest(event, entity, timeout);
    }
    @Override
    public SubscribeStream sendAndSubscribe(String event, Entity entity, long timeout) throws IOException {
        ClientSession sender = this.getSessionAny((String)null);
        return sender.sendAndSubscribe(event, entity, timeout);
    }
    @Override
    public void preclose() throws IOException {
        Iterator var1 = this.sessionList.iterator();

        while(var1.hasNext()) {
            ClientSession session = (ClientSession)var1.next();
            RunUtils.runAndTry(session::preclose);
        }

    }
    @Override
    public void close() throws IOException {
        Iterator var1 = this.sessionList.iterator();

        while(var1.hasNext()) {
            ClientSession session = (ClientSession)var1.next();
            RunUtils.runAndTry(session::close);
        }

    }
    @Override
    public void reconnect() throws IOException {
        Iterator var1 = this.sessionList.iterator();

        while(var1.hasNext()) {
            ClientSession session = (ClientSession)var1.next();
            if (!session.isValid()) {
                session.reconnect();
            }
        }

    }
}
