
package org.yourmq.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yourmq.common.Entity;
import org.yourmq.common.Message;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.RunUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BrokerListener extends BrokerListenerBase implements Listener, BroadcastBroker {
    protected static final Logger log = LoggerFactory.getLogger(BrokerListener.class);

    public BrokerListener() {
    }

    @Override
    public void onOpen(Session session) throws IOException {
        String name = session.name();
        this.addPlayer(name, session);
    }

    @Override
    public void onClose(Session session) {
        String name = session.name();
        this.removePlayer(name, session);
    }

    @Override

    public void onMessage(Session requester, Message message) throws IOException {
        this.onMessageDo(requester, message);
    }

    protected void onMessageDo(Session requester, Message message) throws IOException {
        String atName = message.atName();
        if (atName == null) {
            if (requester != null) {
                requester.sendAlarm(message, "Broker message require '@' meta");
            } else {
                throw new YourSocketException("Broker message require '@' meta");
            }
        } else {
            if (atName.equals("*")) {
                Collection<String> nameAll = this.getNameAll();
                if (nameAll != null && nameAll.size() > 0) {
                    Iterator var5 = (new ArrayList(nameAll)).iterator();

                    while (var5.hasNext()) {
                        String name = (String) var5.next();
                        this.forwardToName(requester, message, name);
                    }
                }
            } else if (atName.endsWith("*")) {
                atName = atName.substring(0, atName.length() - 1);
                if (!this.forwardToName(requester, message, atName)) {
                    if (requester == null) {
                        throw new YourSocketException("Broker don't have '@" + atName + "' player");
                    }

                    requester.sendAlarm(message, "Broker don't have '@" + atName + "' player");
                }
            } else {
                Session responder = this.getPlayerAny(atName, requester, message);
                if (responder != null) {
                    this.forwardToSession(requester, message, responder);
                } else {
                    if (requester == null) {
                        throw new YourSocketException("Broker don't have '@" + atName + "' session");
                    }

                    requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
                }
            }

        }
    }

    @Override
    public void broadcast(String event, Entity entity) throws IOException {
        this.onMessageDo((Session) null, (new MessageBuilder()).flag(40).event(event).entity(entity).build());
    }

    public boolean forwardToName(Session requester, Message message, String name) throws IOException {
        Collection<Session> playerAll = this.getPlayerAll(name);
        if (playerAll != null && playerAll.size() > 0) {
            Iterator var5 = (new ArrayList(playerAll)).iterator();

            while (var5.hasNext()) {
                Session responder = (Session) var5.next();
                if (responder != requester) {
                    if (responder.isValid()) {
                        this.forwardToSession(requester, message, responder);
                    } else {
                        this.onClose(responder);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void forwardToSession(Session requester, Message message, Session responder) throws IOException {
        this.forwardToSession(requester, message, responder, -1L);
    }

    public void forwardToSession(Session requester, Message message, Session responder, long timeout) throws IOException {
        if (message.isRequest()) {
            responder.sendAndRequest(message.event(), message, timeout).thenReply((reply) -> {
                if (SessionUtils.isValid(requester)) {
                    requester.reply(message, reply);
                }

            }).thenError((err) -> {
                if (SessionUtils.isValid(requester)) {
                    RunUtils.runAndTry(() -> {
                        requester.sendAlarm(message, err.getMessage());
                    });
                }

            });
        } else if (message.isSubscribe()) {
            responder.sendAndSubscribe(message.event(), message, timeout).thenReply((reply) -> {
                if (SessionUtils.isValid(requester)) {
                    if (reply.isEnd()) {
                        requester.replyEnd(message, reply);
                    } else {
                        requester.reply(message, reply);
                    }
                }

            }).thenError((err) -> {
                if (SessionUtils.isValid(requester)) {
                    RunUtils.runAndTry(() -> {
                        requester.sendAlarm(message, err.getMessage());
                    });
                }

            });
        } else {
            responder.send(message.event(), message);
        }

    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isWarnEnabled()) {
            log.warn("Broker error", error);
        }

    }
}
