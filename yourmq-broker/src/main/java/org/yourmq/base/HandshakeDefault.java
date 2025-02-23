
package org.yourmq.base;

import org.yourmq.utils.StrUtils;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandshakeDefault implements HandshakeInternal {
    private final MessageInternal source;
    private final URI uri;
    private final String path;
    private final String version;
    private final Map<String, String> paramMap;
    private final Map<String, String> outMetaMap;
    @Override
    public MessageInternal getSource() {
        return this.source;
    }

    public HandshakeDefault(MessageInternal source) {
        String linkUrl = source.dataAsString();
        if (StrUtils.isEmpty(linkUrl)) {
            linkUrl = source.event();
        }

        this.source = source;
        this.uri = URI.create(linkUrl);
        this.version = source.meta("YourSocket");
        this.paramMap = new ConcurrentHashMap();
        this.outMetaMap = new ConcurrentHashMap();
        if (StrUtils.isEmpty(this.uri.getPath())) {
            this.path = "/";
        } else {
            this.path = this.uri.getPath();
        }

        String queryString = this.uri.getQuery();
        if (StrUtils.isNotEmpty(queryString)) {
            String[] var4 = queryString.split("&");
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String kvStr = var4[var6];
                int idx = kvStr.indexOf(61);
                if (idx > 0) {
                    this.paramMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }

        this.paramMap.putAll(source.metaMap());
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public URI uri() {
        return this.uri;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public Map<String, String> paramMap() {
        return this.paramMap;
    }

    @Override
    public String param(String name) {
        return (String) this.paramMap.get(name);
    }

    @Override
    public String paramOrDefault(String name, String def) {
        return (String) this.paramMap.getOrDefault(name, def);
    }

    @Override
    public Handshake paramPut(String name, String value) {
        this.paramMap.put(name, value);
        return this;
    }

    @Override
    public void outMeta(String name, String value) {
        this.outMetaMap.put(name, value);
    }

    @Override
    public Map<String, String> getOutMetaMap() {
        return this.outMetaMap;
    }
}
