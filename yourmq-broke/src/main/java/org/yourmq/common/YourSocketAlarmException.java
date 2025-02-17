package org.yourmq.common;

public class YourSocketAlarmException extends YourSocketException {
    private Message alarm;
    private int alarmCode;

    public Message getAlarm() {
        return this.alarm;
    }

    public int getAlarmCode() {
        return this.alarmCode;
    }

    public YourSocketAlarmException(Message alarm) {
        super(alarm.dataAsString());
        this.alarm = alarm;
        this.alarmCode = alarm.metaAsInt("code");
    }
}