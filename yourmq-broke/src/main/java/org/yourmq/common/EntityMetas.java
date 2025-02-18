package org.yourmq.common;

public interface EntityMetas {
    String META_SOCKETD_VERSION = "Socket.D";
    /**
     * @deprecated
     */
    @Deprecated
    String META_X_REAL_IP = "X-Real-IP";
    String META_X_IP = "X-IP";
    String META_X_HASH = "X-Hash";
    /**
     * @deprecated
     */
    @Deprecated
    String META_X_Hash = "X-Hash";
    String META_X_UNLIMITED = "X-Unlimited";
    String META_DATA_LENGTH = "Data-Length";
    String META_DATA_TYPE = "Data-Type";
    String META_DATA_FRAGMENT_IDX = "Data-Fragment-Idx";
    String META_DATA_FRAGMENT_TOTAL = "Data-Fragment-Total";
    String META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename";
    String META_RANGE_START = "Data-Range-Start";
    String META_RANGE_SIZE = "Data-Range-Size";
}