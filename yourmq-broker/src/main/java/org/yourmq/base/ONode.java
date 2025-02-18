//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.common.DEFAULTS;
import org.yourmq.common.Feature;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ONode {
    protected Options _o;
    protected ONodeData _d;
    protected ONode _p;

    public static String version() {
        return "3.2";
    }

    public ONode() {
        this((ONode) null, (Options) null);
    }

    public ONode(Options options) {
        this((ONode) null, options);
    }

    public ONode(ONode parent, Options options) {
        this._p = parent;
        this._d = new ONodeData(this);
        if (options == null) {
            this._o = Options.def();
        } else {
            this._o = options;
        }

    }

    public static ONode newValue() {
        return (new ONode()).asValue();
    }

    public static ONode newObject() {
        return (new ONode()).asObject();
    }

    public static ONode newArray() {
        return (new ONode()).asArray();
    }

    public ONode parent() {
        return this._p;
    }

    public ONode parents(int depth) {
        ONode tmp;
        for (tmp = this._p; depth > 0 && tmp != null; --depth) {
            tmp = tmp.parent();
        }

        return tmp;
    }

    public ONode select(String jpath, boolean useStandard, boolean cacheJpath) {
        return JsonPath.eval(this, jpath, useStandard, cacheJpath, JsonPath.CRUD.GET);
    }

    public ONode select(String jpath, boolean useStandard) {
        return this.select(jpath, useStandard, true);
    }

    public ONode select(String jpath) {
        return this.select(jpath, false);
    }

    public ONode selectOrNew(String jpath) {
        return JsonPath.eval(this, jpath, false, true, JsonPath.CRUD.GET_OR_NEW);
    }

    public boolean exists(String jpath) {
        return !this.select(jpath).isUndefined();
    }

    public ONode usePaths() {
        JsonPath.resolvePath("$", this);
        return this;
    }

    public String path() {
        return this.attrGet("$PATH");
    }

    public List<String> pathList() {
        List<String> rst = new ArrayList();
        JsonPath.extractPath(rst, this);
        return rst;
    }

    public ONode asObject() {
        this._d.tryInitObject();
        return this;
    }

    public ONode asArray() {
        this._d.tryInitArray();
        return this;
    }

    public ONode asValue() {
        this._d.tryInitValue();
        return this;
    }

    public ONode asNull() {
        this._d.tryInitNull();
        return this;
    }

    public ONodeData nodeData() {
        return this._d;
    }

    public ONodeType nodeType() {
        return this._d.nodeType;
    }

    public ONode options(Options opts) {
        if (opts != null) {
            this._o = opts;
        }

        return this;
    }

    public ONode options(Consumer<Options> custom) {
        custom.accept(this._o);
        return this;
    }

    public Options options() {
        return this._o;
    }

    public ONode build(Consumer<ONode> custom) {
        custom.accept(this);
        return this;
    }

    public OValue val() {
        return this.asValue()._d.value;
    }

    public ONode val(Object val) {
        if (val == null) {
            this._d.tryInitNull();
        } else if (val instanceof ONode) {
            this._d = ((ONode) val)._d;
        } else if (!(val instanceof Map) && !(val instanceof Collection) && !val.getClass().isArray()) {
            this._d.tryInitValue();
            this._d.value.set(val);
        } else {
            this._d = this.buildVal(val)._d;
        }

        return this;
    }

    public String getString() {
        if (this.isValue()) {
            return this._d.value.getString();
        } else if (this.isArray()) {
            return this.toJson();
        } else if (this.isObject()) {
            return this.toJson();
        } else {
            return this._o.hasFeature(Feature.StringNullAsEmpty) ? "" : null;
        }
    }

    public short getShort() {
        return this.isValue() ? this._d.value.getShort() : 0;
    }

    public int getInt() {
        return this.isValue() ? this._d.value.getInt() : 0;
    }

    public long getLong() {
        return this.isValue() ? this._d.value.getLong() : 0L;
    }

    public float getFloat() {
        return this.isValue() ? this._d.value.getFloat() : 0.0F;
    }

    public double getDouble() {
        return this.isValue() ? this._d.value.getDouble() : 0.0;
    }

    public double getDouble(int scale) {
        double temp = this.getDouble();
        return temp == 0.0 ? 0.0 : (new BigDecimal(temp)).setScale(scale, 4).doubleValue();
    }

    public boolean getBoolean() {
        return this.isValue() ? this._d.value.getBoolean() : false;
    }

    public Date getDate() {
        return this.isValue() ? this._d.value.getDate() : null;
    }

    public char getChar() {
        return this.isValue() ? this._d.value.getChar() : '\u0000';
    }

    public String getRawString() {
        return this.isValue() ? this._d.value.getRawString() : null;
    }

    public Number getRawNumber() {
        return this.isValue() ? this._d.value.getRawNumber() : null;
    }

    public Boolean getRawBoolean() {
        return this.isValue() ? this._d.value.getRawBoolean() : null;
    }

    public Date getRawDate() {
        return this.isValue() ? this._d.value.getRawDate() : null;
    }

    public void clear() {
        if (this.isObject()) {
            this._d.object.clear();
        } else if (this.isArray()) {
            this._d.array.clear();
        }

    }

    public int count() {
        if (this.isObject()) {
            return this._d.object.size();
        } else {
            return this.isArray() ? this._d.array.size() : 0;
        }
    }

    public Map<String, ONode> obj() {
        return this.asObject()._d.object;
    }

    public boolean contains(String key) {
        return this.isObject() ? this._d.object.containsKey(key) : false;
    }

    public ONode rename(String key, String newKey) {
        if (key != null && newKey != null) {
            if (!key.equals(newKey)) {
                if (this.isObject()) {
                    rename_do(this, key, newKey);
                } else if (this.isArray()) {
                    Iterator var3 = this._d.array.iterator();

                    while (var3.hasNext()) {
                        ONode n = (ONode) var3.next();
                        rename_do(n, key, newKey);
                    }
                }
            }

            return this;
        } else {
            return this;
        }
    }

    private static void rename_do(ONode n, String key, String newKey) {
        if (n.isObject()) {
            ONode tmp = (ONode) n._d.object.get(key);
            if (tmp != null) {
                n._d.object.put(newKey, tmp);
                n._d.object.remove(key);
            }
        }

    }

    public ONode get(String key) {
        this._d.tryInitObject();
        ONode tmp = (ONode) this._d.object.get(key);
        return tmp == null ? new ONode(this, this._o) : tmp;
    }

    public ONode getOrNew(String key) {
        return this.getOrNew(key, ONodeType.Null);
    }

    public ONode getOrNew(String key, ONodeType newNodeType) {
        this._d.tryInitObject();
        ONode tmp = (ONode) this._d.object.get(key);
        if (tmp == null) {
            tmp = new ONode(this, this._o);
            if (newNodeType == ONodeType.Object) {
                tmp.asObject();
            } else if (newNodeType == ONodeType.Array) {
                tmp.asArray();
            }

            this._d.object.put(key, tmp);
        }

        return tmp;
    }

    public ONode getOrNull(String key) {
        return this.isObject() ? (ONode) this._d.object.get(key) : null;
    }

    public ONode getNew(String key) {
        this._d.tryInitObject();
        ONode tmp = new ONode(this, this._o);
        this._d.object.put(key, tmp);
        return tmp;
    }

    private ONode buildVal(Object val) {
        if (val instanceof Map) {
            return (new ONode(this, this._o)).setAll((Map) val);
        } else if (val instanceof Collection) {
            return (new ONode(this, this._o)).addAll((Collection) val);
        } else {
            return val != null && val.getClass().isArray() ? (new ONode(this, this._o)).addAll((Collection) Arrays.asList((Object[]) ((Object[]) val))) : (new ONode(this, this._o)).val(val);
        }
    }

    public ONode set(String key, Object val) {
        this._d.tryInitObject();
        if (val instanceof ONode) {
            this.setNode(key, (ONode) val);
        } else {
            this.setNode(key, this.buildVal(val));
        }

        return this;
    }

    public ONode setNode(String key, ONode val) {
        this._d.object.put(key, val);
        if (val._p == null) {
            val._p = this;
        }

        return this;
    }

    public ONode setAll(ONode obj) {
        this._d.tryInitObject();
        if (obj != null && obj.isObject()) {
            Iterator var2 = obj._d.object.entrySet().iterator();

            while (var2.hasNext()) {
                Map.Entry<String, ONode> kv = (Map.Entry) var2.next();
                this.setNode((String) kv.getKey(), (ONode) kv.getValue());
            }
        }

        return this;
    }

    public <T> ONode setAll(Map<String, T> map) {
        this._d.tryInitObject();
        if (map != null) {
            map.forEach(this::set);
        }

        return this;
    }

    public <T> ONode setAll(Map<String, T> map, BiConsumer<ONode, T> handler) {
        this._d.tryInitObject();
        if (map != null) {
            map.forEach((k, v) -> {
                handler.accept(this.get(k), v);
            });
        }

        return this;
    }

    public void remove(String key) {
        if (this.isObject()) {
            this._d.object.remove(key);
        }

    }

    public List<ONode> ary() {
        return this.asArray()._d.array;
    }

    public ONode get(int index) {
        this._d.tryInitArray();
        return index >= 0 && this._d.array.size() > index ? (ONode) this._d.array.get(index) : new ONode(this, this._o);
    }

    public ONode getOrNew(int index) {
        return this.getOrNew(index, ONodeType.Null);
    }

    public ONode getOrNew(int index, ONodeType newNodeType) {
        this._d.tryInitArray();
        if (this._d.array.size() > index) {
            return (ONode) this._d.array.get(index);
        } else {
            ONode tmp = null;

            for (int i = this._d.array.size(); i <= index; ++i) {
                tmp = new ONode(this, this._o);
                if (newNodeType == ONodeType.Object) {
                    tmp.asObject();
                } else if (newNodeType == ONodeType.Array) {
                    tmp.asArray();
                }

                this._d.array.add(tmp);
            }

            return tmp;
        }
    }

    public ONode getOrNull(int index) {
        return this.isArray() && index >= 0 && this._d.array.size() > index ? (ONode) this._d.array.get(index) : null;
    }

    public void removeAt(int index) {
        if (this.isArray()) {
            this._d.array.remove(index);
        }

    }

    public ONode addNew() {
        this._d.tryInitArray();
        ONode n = new ONode(this, this._o);
        this._d.array.add(n);
        return n;
    }

    public ONode add(Object val) {
        this._d.tryInitArray();
        if (val instanceof ONode) {
            this.addNode((ONode) val);
        } else {
            this.addNode(this.buildVal(val));
        }

        return this;
    }

    public ONode addNode(ONode val) {
        this._d.array.add(val);
        if (val._p == null) {
            val._p = this;
        }

        return this;
    }

    public ONode addAll(ONode ary) {
        this._d.tryInitArray();
        if (ary != null && ary.isArray()) {
            Iterator var2 = ary._d.array.iterator();

            while (var2.hasNext()) {
                ONode n1 = (ONode) var2.next();
                this.addNode(n1);
            }
        }

        return this;
    }

    public <T> ONode addAll(Collection<T> ary) {
        this._d.tryInitArray();
        if (ary != null) {
            ary.forEach((m) -> {
                this.add(m);
            });
        }

        return this;
    }

    public <T> ONode addAll(Collection<T> ary, BiConsumer<ONode, T> handler) {
        this._d.tryInitArray();
        if (ary != null) {
            ary.forEach((m) -> {
                handler.accept(this.addNew(), m);
            });
        }

        return this;
    }

    public boolean isUndefined() {
        return this._d.nodeType == ONodeType.Null;
    }

    public boolean isNull() {
        return this._d.nodeType == ONodeType.Null || this.isValue() && this._d.value.isNull();
    }

    public boolean isValue() {
        return this._d.nodeType == ONodeType.Value;
    }

    public boolean isObject() {
        return this._d.nodeType == ONodeType.Object;
    }

    public boolean isArray() {
        return this._d.nodeType == ONodeType.Array;
    }

    public ONode forEach(BiConsumer<String, ONode> consumer) {
        if (this.isObject()) {
            this._d.object.forEach(consumer);
        }

        return this;
    }

    public ONode forEach(Consumer<ONode> consumer) {
        if (this.isArray()) {
            this._d.array.forEach(consumer);
        }

        return this;
    }

    public String attrGet(String key) {
        return this._d.attrGet(key);
    }

    public ONode attrSet(String key, String val) {
        this._d.attrSet(key, val);
        return this;
    }

    public ONode attrForeach(BiConsumer<String, String> consumer) {
        if (this._d.attrs != null) {
            this._d.attrs.forEach(consumer);
        }

        return this;
    }

    @Override
    public String toString() {
        return (String) this.to(DEFAULTS.DEF_STRING_TOER);
    }

    public String toJson() {
        return (String) this.to(DEFAULTS.DEF_JSON_TOER);
    }

    public Object toData() {
        return this.to(DEFAULTS.DEF_OBJECT_TOER);
    }

    public <T> T toObject() {
        return this.toObject(Object.class);
    }

    public <T> T toObject(Type clz) {
        return this.to(DEFAULTS.DEF_OBJECT_TOER, clz);
    }

    public <T> List<T> toObjectList(Class<T> clz) {
        List<T> list = new ArrayList();
        Iterator var3 = this.ary().iterator();

        while (var3.hasNext()) {
            ONode n = (ONode) var3.next();
            list.add(n.toObject(clz));
        }

        return list;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> List<T> toArray(Class<T> clz) {
        return this.toObjectList(clz);
    }

    public <T> T to(Toer toer, Type clz) {
        return (T) (new Context(this._o, this, clz)).handle(toer).target;
    }

    public <T> T to(Toer toer) {
        return this.to(toer, (Type) null);
    }

    public <T> T bindTo(T target) {
        Context ctx = new Context(this._o, this, target.getClass());
        ctx.target = target;
        ctx.handle(DEFAULTS.DEF_OBJECT_TOER);
        return target;
    }

    public ONode fill(Object source) {
        this.val(doLoad(source, source instanceof String, this._o, (Fromer) null));
        return this;
    }

    public ONode fill(Object source, Feature... features) {
        this.val(doLoad(source, source instanceof String, Options.def().add(features), (Fromer) null));
        return this;
    }

    public ONode fillObj(Object source, Feature... features) {
        this.val(doLoad(source, false, Options.def().add(features), (Fromer) null));
        return this;
    }

    public ONode fillStr(String source, Feature... features) {
        this.val(doLoad(source, true, Options.def().add(features), (Fromer) null));
        return this;
    }

    public static ONode load(Object source) {
        return load(source, (Options) null, (Fromer) null);
    }

    public static ONode load(Object source, Feature... features) {
        return load(source, Options.def().add(features), (Fromer) null);
    }

    public static ONode load(Object source, Options opts) {
        return load(source, opts, (Fromer) null);
    }

    public static ONode load(Object source, Options opts, Fromer fromer) {
        return doLoad(source, source instanceof String, opts, fromer);
    }

    public static ONode loadStr(String source) {
        return doLoad(source, true, (Options) null, (Fromer) null);
    }

    public static ONode loadStr(String source, Options opts) {
        return doLoad(source, true, opts, (Fromer) null);
    }

    public static ONode loadStr(String source, Feature... features) {
        return doLoad(source, true, Options.def().add(features), (Fromer) null);
    }

    public static ONode loadObj(Object source) {
        return doLoad(source, false, (Options) null, (Fromer) null);
    }

    public static ONode loadObj(Object source, Options opts) {
        return doLoad(source, false, opts, (Fromer) null);
    }

    public static ONode loadObj(Object source, Feature... features) {
        return doLoad(source, false, Options.def().add(features), (Fromer) null);
    }

    private static ONode doLoad(Object source, boolean isString, Options opts, Fromer fromer) {
        if (fromer == null) {
            if (isString) {
                fromer = DEFAULTS.DEF_STRING_FROMER;
            } else {
                fromer = DEFAULTS.DEF_OBJECT_FROMER;
            }
        }

        if (opts == null) {
            opts = Options.def();
        }

        return (ONode) (new Context(opts, source)).handle(fromer).target;
    }

    public static String stringify(Object source) {
        return stringify(source, Options.def());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static String stringify(Object source, Feature... features) {
        return features.length > 0 ? stringify(source, new Options(Feature.of(features))) : stringify(source, Options.def());
    }

    public static String stringify(Object source, Options opts) {
        return load(source, opts, DEFAULTS.DEF_OBJECT_FROMER).toString();
    }

    public static String serialize(Object source) {
        return load(source, Options.serialize(), DEFAULTS.DEF_OBJECT_FROMER).toJson();
    }

    public static <T> T deserialize(String source) {
        return deserialize(source, Object.class);
    }

    public static <T> T deserialize(String source, Type clz) {
        return load(source, Options.serialize(), (Fromer) null).toObject(clz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return this.isNull();
        } else if (this.isArray()) {
            return o instanceof ONode ? Objects.equals(this.ary(), ((ONode) o).ary()) : Objects.equals(this.ary(), o);
        } else if (this.isObject()) {
            return o instanceof ONode ? Objects.equals(this.obj(), ((ONode) o).obj()) : Objects.equals(this.obj(), o);
        } else if (this.isValue()) {
            return o instanceof ONode ? Objects.equals(this.val(), ((ONode) o).val()) : Objects.equals(this.val(), o);
        } else {
            return o instanceof ONode ? ((ONode) o).isNull() : false;
        }
    }

    @Override
    public int hashCode() {
        return this._d.hashCode();
    }
}
