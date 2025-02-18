//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.common.Message;
import org.yourmq.utils.StrUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BrokerListenerBase {
    private Map<String, Session> sessionAll = new ConcurrentHashMap();
    private Map<String, Set<Session>> playerSessions = new ConcurrentHashMap();

    public BrokerListenerBase() {
    }

    public Collection<Session> getSessionAll() {
        return this.sessionAll.values();
    }

    public Session getSessionById(String sessionId) {
        return (Session) this.sessionAll.get(sessionId);
    }

    public Session getSessionAny() {
        return (Session) LoadBalancer.getAnyByPoll(this.sessionAll.values());
    }

    public int getSessionCount() {
        return this.sessionAll.size();
    }

    public Collection<String> getNameAll() {
        return this.playerSessions.keySet();
    }

    public int getPlayerCount(String name) {
        Collection<Session> tmp = this.getPlayerAll(name);
        return tmp == null ? 0 : tmp.size();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int getPlayerNum(String name) {
        return this.getPlayerCount(name);
    }

    public Collection<Session> getPlayerAll(String name) {
        return (Collection) this.playerSessions.get(name);
    }

    public Session getPlayerAny(String atName, Session requester, Message message) throws IOException {
        if (StrUtils.isEmpty(atName)) {
            return null;
        } else if (atName.endsWith("!")) {
            atName = atName.substring(0, atName.length() - 1);
            String x_hash = null;
            if (message != null) {
                x_hash = message.meta("X-Hash");
            }

            if (StrUtils.isEmpty(x_hash)) {
                return requester == null ? (Session) LoadBalancer.getAnyByPoll(this.getPlayerAll(atName)) : (Session) LoadBalancer.getAnyByHash(this.getPlayerAll(atName), requester.remoteAddress().getHostName());
            } else {
                return (Session) LoadBalancer.getAnyByHash(this.getPlayerAll(atName), x_hash);
            }
        } else {
            return (Session) LoadBalancer.getAnyByPoll(this.getPlayerAll(atName));
        }
    }

    public Session getPlayerAny(String atName) {
        if (StrUtils.isEmpty(atName)) {
            return null;
        } else {
            if (atName.endsWith("!")) {
                atName = atName.substring(0, atName.length() - 1);
            }

            return (Session) LoadBalancer.getAnyByPoll(this.getPlayerAll(atName));
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Session getPlayerOne(String atName) {
        return this.getPlayerAny(atName);
    }

    public void addPlayer(String name, Session session) {
        if (StrUtils.isNotEmpty(name)) {
            Set<Session> sessions = (Set) this.playerSessions.computeIfAbsent(name, (n) -> {
                return Collections.newSetFromMap(new ConcurrentHashMap());
            });
            sessions.add(session);
        }

        this.sessionAll.put(session.sessionId(), session);
    }

    public void removePlayer(String name, Session session) {
        if (StrUtils.isNotEmpty(name)) {
            Collection<Session> sessions = this.getPlayerAll(name);
            if (sessions != null) {
                sessions.remove(session);
            }
        }

        this.sessionAll.remove(session.sessionId());
    }
}
