package org.yourmq.common;

import org.yourmq.exception.YourSocketException;

public class YourMQConnectionException extends YourSocketException {
    public YourMQConnectionException(String message) {
        super(message);
    }

    public YourMQConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}