package org.yourmq.client.base;

import java.net.URI;
import java.util.Map;

public interface Handshake {
    String version();

    URI uri();

    String path();

    Map<String, String> paramMap();

    String param(String name);

    String paramOrDefault(String name, String def);

    Handshake paramPut(String name, String value);

    void outMeta(String name, String value);
}