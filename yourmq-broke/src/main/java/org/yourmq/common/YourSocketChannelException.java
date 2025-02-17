package org.yourmq.common;

public class YourSocketChannelException extends YourSocketException {
    public YourSocketChannelException(String message) {
        super(message);
    }

    public YourSocketChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
