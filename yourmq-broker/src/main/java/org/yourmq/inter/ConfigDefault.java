package org.yourmq.inter;

import org.yourmq.base.TrafficLimiter;

public class ConfigDefault extends ConfigBase<ConfigDefault> {

    public ConfigDefault(boolean clientMode) {
        super(clientMode);
    }

    @Override
    public boolean isNolockSend() {
        return false;
    }

    @Override
    public TrafficLimiter getTrafficLimiter() {
        return null;
    }
}
