package org.yourmq.snap;

import org.yourmq.base.BaseServerProps;
import org.yourmq.utils.Utils;

public class HttpServerProps extends BaseServerProps {
    private static HttpServerProps instance;

    public static HttpServerProps getInstance() {
        if (instance == null) {
            instance = new HttpServerProps();
        }

        return instance;
    }

    public HttpServerProps() {
        super("http", 0);
    }

    public String buildHttpServerUrl(boolean isSecure) {
        StringBuilder buf = new StringBuilder();
        buf.append(isSecure ? "https" : "http");
        buf.append("://");
        if (Utils.isEmpty(this.getHost())) {
            buf.append("localhost");
        } else {
            buf.append(this.getHost());
        }

        buf.append(":");
        buf.append(this.getPort());
        return buf.toString();
    }

    public String buildWsServerUrl(boolean isSecure) {
        StringBuilder buf = new StringBuilder();
        buf.append(isSecure ? "wws" : "ws");
        buf.append("://");
        if (Utils.isEmpty(this.getHost())) {
            buf.append("localhost");
        } else {
            buf.append(this.getHost());
        }

        buf.append(":");
        buf.append(this.getPort());
        return buf.toString();
    }
}