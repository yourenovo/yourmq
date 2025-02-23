
package org.yourmq.base;

import org.yourmq.common.*;

import java.util.Map;

public class Frames {
    public Frames() {
    }

    public static final Frame connectFrame(String sid, String url, Map<String, String> metaMap) {
        StringEntity entity = new StringEntity(url);
        entity.metaMapPut(metaMap);
        entity.metaPut("YourSocket", YourSocket.protocolVersion());
        return new Frame(10, (new MessageBuilder()).sid(sid).event(url).entity(entity).build());
    }

    public static final Frame connackFrame(HandshakeInternal handshake) {
        EntityDefault entity = new EntityDefault();
        entity.metaMapPut(handshake.getOutMetaMap());
        entity.metaPut("YourSocket", YourSocket.protocolVersion());
        entity.dataSet(handshake.getSource().entity().data());
        return new Frame(11, (new MessageBuilder()).sid(handshake.getSource().sid()).event(handshake.getSource().event()).entity(entity).build());
    }

    public static final Frame pingFrame() {
        return new Frame(20, (MessageInternal)null);
    }

    public static final Frame pongFrame() {
        return new Frame(21, (MessageInternal)null);
    }

    public static final Frame closeFrame(int code) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.entity(Entity.of().metaPut("code", String.valueOf(code)));
        return new Frame(30, messageBuilder.build());
    }

    public static final Frame alarmFrame(Message from, Entity alarm) {
        MessageBuilder messageBuilder = new MessageBuilder();
        if (from != null) {
            EntityDefault entity = new EntityDefault();
            entity.metaStringSet(from.metaString());
            entity.dataSet(alarm.data());
            entity.metaMapPut(alarm.metaMap());
            messageBuilder.sid(from.sid());
            messageBuilder.event(from.event());
            messageBuilder.entity(entity);
        } else {
            messageBuilder.entity(alarm);
        }

        return new Frame(31, messageBuilder.build());
    }

    public static final Frame pressureFrame(Message from, Entity pressure) {
        MessageBuilder messageBuilder = new MessageBuilder();
        if (from != null) {
            EntityDefault entity = new EntityDefault();
            entity.metaStringSet(from.metaString());
            entity.dataSet(pressure.data());
            entity.metaMapPut(pressure.metaMap());
            messageBuilder.sid(from.sid());
            messageBuilder.event(from.event());
            messageBuilder.entity(entity);
        } else {
            messageBuilder.entity(pressure);
        }

        return new Frame(32, messageBuilder.build());
    }
}
