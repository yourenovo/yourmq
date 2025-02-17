//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

public interface Flags {
    int Unknown = 0;
    int Connect = 10;
    int Connack = 11;
    int Ping = 20;
    int Pong = 21;
    int Close = 30;
    int Alarm = 31;
    int Pressure = 32;
    int Message = 40;
    int Request = 41;
    int Subscribe = 42;
    int Reply = 48;
    int ReplyEnd = 49;

    static int of(int code) {
        switch (code) {
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            default:
                return 0;
            case 20:
                return 20;
            case 21:
                return 21;
            case 30:
                return 30;
            case 31:
                return 31;
            case 32:
                return 32;
            case 40:
                return 40;
            case 41:
                return 41;
            case 42:
                return 42;
            case 48:
                return 48;
            case 49:
                return 49;
        }
    }

    static String name(int code) {
        switch (code) {
            case 10:
                return "Connect";
            case 11:
                return "Connack";
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            default:
                return "Unknown";
            case 20:
                return "Ping";
            case 21:
                return "Pong";
            case 30:
                return "Close";
            case 31:
                return "Alarm";
            case 32:
                return "Pressure";
            case 40:
                return "Message";
            case 41:
                return "Request";
            case 42:
                return "Subscribe";
            case 48:
                return "Reply";
            case 49:
                return "ReplyEnd";
        }
    }
}
