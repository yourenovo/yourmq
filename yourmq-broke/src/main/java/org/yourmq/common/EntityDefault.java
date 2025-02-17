
package org.yourmq.common;

import org.yourmq.utils.StrUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityDefault implements Entity {
    private Map<String, String> metaMap;
    private String metaString = "";
    private boolean metaStringChanged = false;
    private ByteBuffer data;
    private int dataSize;
    private String dataAsString;

    public EntityDefault() {
        this.data = Constants.DEF_DATA;
        this.dataSize = 0;
    }

    public EntityDefault at(String name) {
        this.metaPut("@", name);
        return this;
    }

    public EntityDefault range(int start, int size) {
        this.metaPut("Data-Range-Start", String.valueOf(start));
        this.metaPut("Data-Range-Size", String.valueOf(size));
        return this;
    }

    public EntityDefault metaStringSet(String metaString) {
        this.metaMap = null;
        this.metaString = metaString;
        this.metaStringChanged = false;
        return this;
    }

    public String metaString() {
        if (this.metaStringChanged) {
            StringBuilder buf = new StringBuilder();
            List<String> metaKeys = new ArrayList(this.metaMap().keySet());
            Iterator var3 = metaKeys.iterator();

            while(var3.hasNext()) {
                String name = (String)var3.next();
                String val = (String)this.metaMap().get(name);
                buf.append(name).append("=").append(val).append("&");
            }

            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }

            this.metaString = buf.toString();
            this.metaStringChanged = false;
        }

        return this.metaString;
    }

    public EntityDefault metaMapPut(Map<String, String> metaMap) {
        if (metaMap != null && metaMap.size() > 0) {
            this.metaMap().putAll(metaMap);
            this.metaStringChanged = true;
        }

        return this;
    }

    public Map<String, String> metaMap() {
        if (this.metaMap == null) {
            this.metaMap = new ConcurrentHashMap();
            this.metaStringChanged = false;
            if (StrUtils.isNotEmpty(this.metaString)) {
                String[] var1 = this.metaString.split("&");
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    String kvStr = var1[var3];
                    int idx = kvStr.indexOf(61);
                    if (idx > 0) {
                        this.metaMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                    }
                }
            }
        }

        return this.metaMap;
    }

    public EntityDefault metaPut(String name, String val) {
        if (val == null) {
            this.metaMap().remove(name);
        } else {
            this.metaMap().put(name, val);
        }

        this.metaStringChanged = true;
        return this;
    }

    public EntityDefault metaDel(String name) {
        this.metaMap().remove(name);
        this.metaStringChanged = true;
        return this;
    }

    public String meta(String name) {
        return (String)this.metaMap().get(name);
    }

    public String metaOrDefault(String name, String def) {
        return (String)this.metaMap().getOrDefault(name, def);
    }

    public void putMeta(String name, String val) {
        this.metaPut(name, val);
    }

    public void delMeta(String name) {
        this.metaDel(name);
    }

    public EntityDefault dataSet(byte[] data) {
        this.data = ByteBuffer.wrap(data);
        this.dataSize = data.length;
        return this;
    }

    public EntityDefault dataSet(ByteBuffer data) {
        this.data = data;
        this.dataSize = data.limit();
        return this;
    }

    public ByteBuffer data() {
        return this.data;
    }

    public String dataAsString() {
        if (this.dataAsString == null) {
            this.dataAsString = new String(this.dataAsBytes(), StandardCharsets.UTF_8);
        }

        return this.dataAsString;
    }

    public byte[] dataAsBytes() {
        if (this.data instanceof MappedByteBuffer) {
            byte[] tmp = new byte[this.dataSize];
            this.data.mark();
            this.data.get(tmp);
            this.data.reset();
            return tmp;
        } else {
            return this.data.array();
        }
    }

    public int dataSize() {
        return this.dataSize;
    }

    public void release() throws IOException {
    }

    public String toString() {
        return "Entity{meta='" + this.metaString() + '\'' + ", data=byte[" + this.dataSize + ']' + '}';
    }
}
