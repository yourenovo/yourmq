package org.yourmq.common;

public class YourSocketException extends RuntimeException {
    public YourSocketException(String message) {
        super(message);
    }

    public YourSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public YourSocketException(Throwable cause) {
        super(cause);
    }
}