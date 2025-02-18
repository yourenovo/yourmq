package org.yourmq.exception;

public class YourSocketChannelException extends YourSocketException {
    public YourSocketChannelException(String message) {
        super(message);
    }

    public YourSocketChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
