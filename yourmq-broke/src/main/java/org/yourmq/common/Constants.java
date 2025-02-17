package org.yourmq.common;

import java.nio.ByteBuffer;

public interface Constants {
    String DEF_SID = "";
    String DEF_EVENT = "";
    String DEF_META_STRING = "";
    ByteBuffer DEF_DATA = ByteBuffer.wrap(new byte[0]);
    int DEF_PORT = 8602;
    int MAX_SIZE_SID = 64;
    int MAX_SIZE_EVENT = 512;
    int MAX_SIZE_META_STRING = 4096;
    int MAX_SIZE_DATA = 16777216;
    int MAX_SIZE_FRAME = 17825792;
    int MIN_FRAGMENT_SIZE = 1024;
    int DEMANDS_ZERO = 0;
    int DEMANDS_SINGLE = 1;
    int DEMANDS_MULTIPLE = 2;
    int CLOSE1000_PROTOCOL_CLOSE_STARTING = 1000;
    int CLOSE1001_PROTOCOL_CLOSE = 1001;
    int CLOSE1002_PROTOCOL_ILLEGAL = 1002;
    int CLOSE2001_ERROR = 2001;
    int CLOSE2002_RECONNECT = 2002;
    int CLOSE2003_DISCONNECTION = 2003;
    int CLOSE2008_OPEN_FAIL = 2008;
    int CLOSE2009_USER = 2009;
    int ALARM3001_PRESSURE = 3001;
}