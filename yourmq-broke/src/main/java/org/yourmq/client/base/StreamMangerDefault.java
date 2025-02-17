package org.yourmq.client.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamMangerDefault implements StreamManger {
    private final Config config;
    private final Map<String, StreamInternal> streamMap = new ConcurrentHashMap();

    public StreamMangerDefault(Config config) {
        this.config = config;
    }
@Override
    public StreamInternal getStream(String sid) {
        return (StreamInternal)this.streamMap.get(sid);
    }
    @Override
    public void addStream(String sid, StreamInternal stream) {

        if (stream.demands() != 0) {
            this.streamMap.put(sid, stream);
            long streamTimeout = stream.timeout() > 0L ? stream.timeout() : this.config.getStreamTimeout();
            if (streamTimeout > 0L) {
                stream.insuranceStart(this, streamTimeout);
            }

        }
    }
    @Override
    public void removeStream(String sid) {
        StreamInternal stream = (StreamInternal)this.streamMap.remove(sid);
        if (stream != null) {
            stream.insuranceCancel();
        }

    }
}
