
package org.yourmq.base;

import org.yourmq.common.DEFAULTS;
import org.yourmq.common.Feature;
import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.BeanUtil;
import org.yourmq.utils.GenericUtil;
import org.yourmq.utils.StringUtil;
import org.yourmq.utils.TypeUtil;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class ObjectToer implements Toer {
    public ObjectToer() {
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ONode o = (ONode) ctx.source;
        if (null != o) {
            ctx.target = this.analyse(ctx, o, ctx.target, ctx.target_clz, ctx.target_type, (Map) null);
        }

    }

    private Object analyse(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (o == null) {
            return rst;
        } else if (clz != null && ONode.class.isAssignableFrom(clz)) {
            return o;
        } else if (o.isNull()) {
            return rst;
        } else {
            if (o.isObject() || o.isArray()) {
                AtomicReference<ONode> oRef = new AtomicReference(o);
                clz = this.getTypeByNode(ctx, oRef, clz);
                o = (ONode) oRef.get();
            }

            if (clz != null) {
                Iterator var9 = ctx.options.decoders().iterator();

                while (var9.hasNext()) {
                    NodeDecoderEntity decoder = (NodeDecoderEntity) var9.next();
                    if (decoder.isDecodable(clz)) {
                        return decoder.decode(o, clz);
                    }
                }
            }

            if (String.class == clz) {
                return o.getString();
            } else {
                switch (o.nodeType()) {
                    case Value:
                        if (clz != null && Collection.class.isAssignableFrom(clz)) {
                            if (TypeUtil.isEmptyCollection(rst) || ctx.options.hasFeature(Feature.DisableCollectionDefaults)) {
                                rst = TypeUtil.createCollection(clz, false);
                            }

                            if (rst != null) {
                                Type type1 = TypeUtil.getCollectionItemType(type);
                                Object val1;
                                if (type1 instanceof Class) {
                                    val1 = this.analyseVal(ctx, o.nodeData(), (Class) type1);
                                    ((Collection) rst).add(val1);
                                    return rst;
                                }

                                val1 = this.analyseVal(ctx, o.nodeData(), (Class) null);
                                ((Collection) rst).add(val1);
                                return rst;
                            }
                        }

                        return this.analyseVal(ctx, o.nodeData(), clz);
                    case Object:
                        o.remove(ctx.options.getTypePropertyName());
                        if (Properties.class.isAssignableFrom(clz)) {
                            return this.analyseProps(ctx, o, (Properties) rst, clz, type, genericInfo);
                        } else if (Map.class.isAssignableFrom(clz)) {
                            return this.analyseMap(ctx, o, (Map) rst, clz, type, genericInfo);
                        } else {
                            if (StackTraceElement.class.isAssignableFrom(clz)) {
                                String declaringClass = o.get("declaringClass").getString();
                                if (declaringClass == null) {
                                    declaringClass = o.get("className").getString();
                                }

                                return new StackTraceElement(declaringClass, o.get("methodName").getString(), o.get("fileName").getString(), o.get("lineNumber").getInt());
                            }

                            if (type instanceof ParameterizedType) {
                                genericInfo = GenericUtil.getGenericInfo(type);
                            }

                            return this.analyseBean(ctx, o, rst, clz, type, genericInfo);
                        }
                    case Array:
                        if (clz.isArray()) {
                            return this.analyseArray(ctx, o.nodeData(), clz);
                        } else {
                            if (rst instanceof Collection) {
                                return this.analyseCollection(ctx, o, (Collection) rst, clz, type, genericInfo);
                            }

                            return this.analyseCollection(ctx, o, (Collection) null, clz, type, genericInfo);
                        }
                    default:
                        return rst;
                }
            }
        }
    }

    private boolean is(Class<?> s, Class<?> t) {
        return s.isAssignableFrom(t);
    }

    public Object analyseVal(Context ctx, ONodeData d, Class<?> clz) throws Exception {
        OValue v = d.value;
        if (v.type() == OValueType.Null) {
            return null;
        } else if (clz == null) {
            return v.getRaw();
        } else if (clz == Byte.TYPE) {
            return (byte) ((int) v.getLong());
        } else if (clz == Short.TYPE) {
            return v.getShort();
        } else if (clz == Integer.TYPE) {
            return v.getInt();
        } else if (clz == Long.TYPE) {
            return v.getLong();
        } else if (clz == Float.TYPE) {
            return v.getFloat();
        } else if (clz == Double.TYPE) {
            return v.getDouble();
        } else if (clz == Boolean.TYPE) {
            return v.getBoolean();
        } else if (clz == Character.TYPE) {
            return v.getChar();
        } else if (this.is(Byte.class, clz)) {
            return v.isEmpty() ? null : (byte) ((int) v.getLong());
        } else if (this.is(Short.class, clz)) {
            return v.isEmpty() ? null : v.getShort();
        } else if (this.is(Integer.class, clz)) {
            return v.isEmpty() ? null : v.getInt();
        } else if (this.is(Long.class, clz)) {
            return v.isEmpty() ? null : v.getLong();
        } else if (this.is(Float.class, clz)) {
            return v.isEmpty() ? null : v.getFloat();
        } else if (this.is(Double.class, clz)) {
            return v.isEmpty() ? null : v.getDouble();
        } else if (this.is(Boolean.class, clz)) {
            return v.isEmpty() ? null : v.getBoolean();
        } else if (this.is(Character.class, clz)) {
            return v.isEmpty() ? null : v.getChar();
        } else if (this.is(Duration.class, clz)) {
            if (v.isEmpty()) {
                return null;
            } else {
                String tmp = v.getString().toUpperCase();
                if (tmp.indexOf(80) != 0) {
                    if (tmp.indexOf(68) > 0) {
                        tmp = "P" + tmp;
                    } else {
                        tmp = "PT" + tmp;
                    }
                }

                return Duration.parse(tmp);
            }
        } else if (this.is(LongAdder.class, clz)) {
            LongAdder tmp = new LongAdder();
            tmp.add(v.getLong());
            return tmp;
        } else if (this.is(DoubleAdder.class, clz)) {
            DoubleAdder tmp = new DoubleAdder();
            tmp.add(v.getDouble());
            return tmp;
        } else if (this.is(String.class, clz)) {
            return v.getString();
        } else if (this.is(URI.class, clz)) {
            return URI.create(v.getString());
        } else if (this.is(Timestamp.class, clz)) {
            return new Timestamp(v.getLong());
        } else if (this.is(Date.class, clz)) {
            return new Date(v.getLong());
        } else if (this.is(Time.class, clz)) {
            return new Time(v.getLong());
        } else if (this.is(java.util.Date.class, clz)) {
            return v.getDate();
        } else {
            java.util.Date date;
            if (this.is(OffsetDateTime.class, clz)) {
                date = v.getDate();
                return null == date ? null : OffsetDateTime.ofInstant(date.toInstant(), DEFAULTS.DEF_TIME_ZONE.toZoneId());
            } else if (this.is(ZonedDateTime.class, clz)) {
                date = v.getDate();
                return null == date ? null : ZonedDateTime.ofInstant(date.toInstant(), DEFAULTS.DEF_TIME_ZONE.toZoneId());
            } else if (this.is(LocalDateTime.class, clz)) {
                date = v.getDate();
                return date == null ? null : date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDateTime();
            } else if (this.is(LocalDate.class, clz)) {
                date = v.getDate();
                return date == null ? null : date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDate();
            } else if (this.is(LocalTime.class, clz)) {
                date = v.getDate();
                return date == null ? null : date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalTime();
            } else if (this.is(OffsetTime.class, clz)) {
                date = v.getDate();
                if (date != null) {
                    return date.toInstant().atOffset(DEFAULTS.DEF_OFFSET).toOffsetTime();
                } else {
                    String dateStr = v.getString();
                    boolean haveOffset = dateStr.contains("+") || dateStr.contains("-") || dateStr.contains("Z");
                    return haveOffset ? OffsetTime.parse(dateStr) : OffsetTime.parse(dateStr + DEFAULTS.DEF_OFFSET);
                }
            } else if (this.is(BigDecimal.class, clz)) {
                return v.type() == OValueType.Number && v.getRawNumber() instanceof BigDecimal ? v.getRawNumber() : new BigDecimal(v.getString());
            } else if (this.is(BigInteger.class, clz)) {
                return v.type() == OValueType.Number && v.getRawNumber() instanceof BigInteger ? v.getRawNumber() : new BigInteger(v.getString());
            } else if (clz.isEnum()) {
                return v.isEmpty() ? null : this.analyseEnum(ctx, d, clz);
            } else if (this.is(Class.class, clz)) {
                return ctx.options.loadClass(v.getString());
            } else if (this.is(File.class, clz)) {
                return new File(v.getString());
            } else if (this.is(Charset.class, clz)) {
                return Charset.forName(v.getString());
            } else if (!this.is(Object.class, clz)) {
                throw new YourSocketException("Unsupport type, class: " + clz.getName());
            } else {
                Object val = v.getRaw();
                if (!(val instanceof String) || !clz.isInterface() && !Modifier.isAbstract(clz.getModifiers())) {
                    return val;
                } else {
                    Class<?> valClz = ctx.options.loadClass((String) val);
                    return valClz == null ? null : BeanUtil.newInstance(valClz);
                }
            }
        }
    }

    public Object analyseEnum(Context ctx, ONodeData d, Class<?> target) {
        EnumWrap ew = TypeUtil.createEnum(target);
        String valString = d.value.getString();
        Enum eItem;
        if (ew.hasCustom()) {
            eItem = ew.getCustom(valString);
        } else if (d.value.type() == OValueType.String) {
            eItem = ew.get(valString);
        } else {
            eItem = ew.get(d.value.getInt());
        }

        if (eItem == null) {
            throw new YourSocketException("Deserialize failure for '" + ew.enumClass().getName() + "' from value: " + valString);
        } else {
            return eItem;
        }
    }

    public Object analyseArray(Context ctx, ONodeData d, Class<?> target) throws Exception {
        int len = d.array.size();
        int i;
        if (this.is(byte[].class, target)) {
            byte[] val = new byte[len];

            for (i = 0; i < len; ++i) {
                val[i] = (byte) ((int) ((ONode) d.array.get(i)).getLong());
            }

            return val;
        } else if (this.is(short[].class, target)) {
            short[] val = new short[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getShort();
            }

            return val;
        } else if (this.is(int[].class, target)) {
            int[] val = new int[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getInt();
            }

            return val;
        } else if (this.is(long[].class, target)) {
            long[] val = new long[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getLong();
            }

            return val;
        } else if (this.is(float[].class, target)) {
            float[] val = new float[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getFloat();
            }

            return val;
        } else if (this.is(double[].class, target)) {
            double[] val = new double[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getDouble();
            }

            return val;
        } else if (this.is(boolean[].class, target)) {
            boolean[] val = new boolean[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getBoolean();
            }

            return val;
        } else if (this.is(char[].class, target)) {
            char[] val = new char[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getChar();
            }

            return val;
        } else if (this.is(String[].class, target)) {
            String[] val = new String[len];

            for (i = 0; i < len; ++i) {
                val[i] = ((ONode) d.array.get(i)).getString();
            }

            return val;
        } else if (!this.is(Object[].class, target)) {
            throw new YourSocketException("Unsupport type, class: " + target.getName());
        } else {
            Class<?> c = target.getComponentType();
            Object[] val = (Object[]) ((Object[]) Array.newInstance(c, len));

            for (int i1 = 0; i1 < len; ++i1) {
                val[i1] = this.analyse(ctx, (ONode) d.array.get(i1), (Object) null, c, c, (Map) null);
            }

            return val;
        }
    }

    public Object analyseCollection(Context ctx, ONode o, Collection coll, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (TypeUtil.isEmptyCollection(coll) || ctx.options.hasFeature(Feature.DisableCollectionDefaults)) {
            coll = TypeUtil.createCollection(clz, false);
        }

        if (coll == null) {
            return coll;
        } else {
            Class<?> itemClz = null;
            Type itemType = null;
            if (ctx.target_type != null) {
                itemType = TypeUtil.getCollectionItemType(type);
                if (itemType instanceof Class) {
                    itemClz = (Class) itemType;
                } else if (itemType instanceof ParameterizedType) {
                    itemClz = (Class) ((ParameterizedType) itemType).getRawType();
                }
            }

            if (itemType != null && itemType instanceof TypeVariable) {
                itemType = null;
            }

            Iterator var9 = o.nodeData().array.iterator();

            while (var9.hasNext()) {
                ONode o1 = (ONode) var9.next();
                coll.add(this.analyse(ctx, o1, (Object) null, itemClz, itemType, genericInfo));
            }

            return coll;
        }
    }

    public Object analyseProps(Context ctx, ONode o, Properties rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (rst == null) {
            rst = new Properties();
        }

        String prefix = "";
        this.propsLoad0(rst, prefix, o);
        return rst;
    }

    private void propsLoad0(Properties props, String prefix, ONode tmp) {
        if (tmp.isObject()) {
            tmp.forEach((k, vx) -> {
                String prefix2 = prefix + "." + k;
                this.propsLoad0(props, prefix2, vx);
            });
        } else if (!tmp.isArray()) {
            if (tmp.isNull()) {
                this.propsPut0(props, prefix, "");
            } else {
                this.propsPut0(props, prefix, tmp.getString());
            }

        } else {
            int index = 0;

            for (Iterator var5 = tmp.ary().iterator(); var5.hasNext(); ++index) {
                ONode v = (ONode) var5.next();
                String prefix2 = prefix + "[" + index + "]";
                this.propsLoad0(props, prefix2, v);
            }

        }
    }

    private void propsPut0(Properties props, String key, Object val) {
        if (key.startsWith(".")) {
            props.put(key.substring(1), val);
        } else {
            props.put(key, val);
        }

    }

    public Object analyseMap(Context ctx, ONode o, Map<Object, Object> map, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (TypeUtil.isEmptyCollection(map) || ctx.options.hasFeature(Feature.DisableCollectionDefaults)) {
            map = TypeUtil.createMap(clz);
        }

        if (map == null) {
            return map;
        } else {
            if (type instanceof ParameterizedType) {
                ParameterizedType ptt = (ParameterizedType) type;
                Type kType = ptt.getActualTypeArguments()[0];
                Type vType = ptt.getActualTypeArguments()[1];
                Class<?> vClass = null;
                if (kType instanceof ParameterizedType) {
                    kType = ((ParameterizedType) kType).getRawType();
                }

                if (vType instanceof Class) {
                    vClass = (Class) vType;
                } else if (vType instanceof ParameterizedType) {
                    vClass = (Class) ((ParameterizedType) vType).getRawType();
                }

                Iterator var11;
                Map.Entry kv;
                if (kType == String.class) {
                    var11 = o.nodeData().object.entrySet().iterator();

                    while (var11.hasNext()) {
                        kv = (Map.Entry) var11.next();
                        map.put(kv.getKey(), this.analyse(ctx, (ONode) kv.getValue(), (Object) null, vClass, vType, genericInfo));
                    }
                } else {
                    var11 = o.nodeData().object.entrySet().iterator();

                    while (var11.hasNext()) {
                        kv = (Map.Entry) var11.next();
                        map.put(TypeUtil.strTo((String) kv.getKey(), (Class) kType), this.analyse(ctx, (ONode) kv.getValue(), (Object) null, vClass, vType, genericInfo));
                    }
                }
            } else {
                Iterator var13 = o.nodeData().object.entrySet().iterator();

                while (var13.hasNext()) {
                    Map.Entry<String, ONode> kv = (Map.Entry) var13.next();
                    map.put(kv.getKey(), this.analyse(ctx, (ONode) kv.getValue(), (Object) null, (Class) null, (Type) null, genericInfo));
                }
            }

            return map;
        }
    }

    public Object analyseBean(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (this.is(SimpleDateFormat.class, clz)) {
            return new SimpleDateFormat(o.get("val").getString());
        } else if (this.is(InetSocketAddress.class, clz)) {
            return new InetSocketAddress(o.get("address").getString(), o.get("port").getInt());
        } else {
            if (this.is(Throwable.class, clz)) {
                String message = o.get("message").getString();
                if (!StringUtil.isEmpty(message)) {
                    try {
                        Constructor fun = clz.getConstructor(String.class);
                        rst = fun.newInstance(message);
                    } catch (Throwable var21) {
                    }
                }
            }

            ClassWrap clzWrap = ClassWrap.get(clz);
            if (clzWrap.recordable()) {
                Parameter[] argsP = clzWrap.recordParams();
                Object[] argsV = new Object[argsP.length];

                for (int j = 0; j < argsP.length; ++j) {
                    Parameter f = argsP[j];
                    String fieldK = f.getName();
                    if (o.contains(fieldK)) {
                        Class fieldT = f.getType();
                        Type fieldGt = f.getParameterizedType();
                        Object val = this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, (Object) null, genericInfo);
                        argsV[j] = val;
                    }
                }

                try {
                    rst = clzWrap.recordConstructor().newInstance(argsV);
                } catch (IllegalArgumentException var19) {
                    throw new IllegalArgumentException("the constructor missing parameters: " + clz.getName(), var19);
                } catch (Throwable var20) {
                    throw new YourSocketException("The instantiation failed, class: " + clz.getName(), var20);
                }
            } else {
                Set<String> excNames = null;
                if (rst == null) {
                    if (clzWrap.recordConstructor() == null) {
                        rst = BeanUtil.newInstance(clz);
                    } else {
                        excNames = new LinkedHashSet();
                        Parameter[] argsP = clzWrap.recordParams();
                        Object[] argsV = new Object[argsP.length];

                        for (int j = 0; j < argsP.length; ++j) {
                            Parameter f = argsP[j];
                            String fieldK = f.getName();
                            excNames.add(fieldK);
                            if (o.contains(fieldK)) {
                                Class fieldT = f.getType();
                                Type fieldGt = f.getParameterizedType();
                                Object val = this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, (Object) null, genericInfo);
                                argsV[j] = val;
                            }
                        }

                        try {
                            rst = clzWrap.recordConstructor().newInstance(argsV);
                        } catch (IllegalArgumentException var17) {
                            throw new IllegalArgumentException("The constructor missing parameters: " + clz.getName(), var17);
                        } catch (Throwable var18) {
                            throw new YourSocketException("The instantiation failed, class: " + clz.getName(), var18);
                        }
                    }
                }

                if (rst == null) {
                    return null;
                }

                boolean useSetter = ctx.options.hasFeature(Feature.UseSetter);
                boolean useOnlySetter = ctx.options.hasFeature(Feature.UseOnlySetter);
                if (useOnlySetter) {
                    useSetter = true;
                }

                boolean useGetter = ctx.options.hasFeature(Feature.UseGetter);
                boolean useOnlyGetter = ctx.options.hasFeature(Feature.UseOnlyGetter);
                if (useOnlyGetter) {
                    useGetter = true;
                }

                Iterator var34;
                if (useSetter) {
                    var34 = o.obj().entrySet().iterator();

                    while (true) {
                        FieldWrap f;
                        label109:
                        do {
                            while (var34.hasNext()) {
                                Map.Entry<String, ONode> kv = (Map.Entry) var34.next();
                                f = clzWrap.getFieldWrap((String) kv.getKey());
                                if (f != null) {
                                    continue label109;
                                }

                                Method m = clzWrap.getProperty((String) kv.getKey());
                                if (m != null) {
                                    this.setValueForMethod(ctx, o, rst, genericInfo, (String) kv.getKey(), m);
                                }
                            }

                            return rst;
                        } while (useOnlySetter && !f.hasSetter);

                        this.setValueForField(ctx, o, rst, genericInfo, f, useSetter, useGetter, excNames);
                    }
                } else {
                    var34 = clzWrap.fieldAllWraps().iterator();

                    while (true) {
                        FieldWrap f;
                        do {
                            if (!var34.hasNext()) {
                                return rst;
                            }

                            f = (FieldWrap) var34.next();
                        } while (useOnlySetter && !f.hasSetter);

                        this.setValueForField(ctx, o, rst, genericInfo, f, useSetter, useGetter, excNames);
                    }
                }
            }

            return rst;
        }
    }

    private void setValueForMethod(Context ctx, ONode o, Object rst, Map<String, Type> genericInfo, String name, Method method) throws Exception {
        Class<?> fieldT = method.getParameterTypes()[0];
        Object val = this.analyseBeanOfValue(name, fieldT, (Type) null, ctx, o, (Object) null, genericInfo);
        if (val == null && ctx.options.hasFeature(Feature.StringFieldInitEmpty) && fieldT == String.class) {
            val = "";
        }

        method.invoke(rst, val);
    }

    private void setValueForField(Context ctx, ONode o, Object rst, Map<String, Type> genericInfo, FieldWrap f, boolean useSetter, boolean useGetter, Set<String> excNames) throws Exception {
        if (f.isDeserialize()) {
            String fieldK = f.getName();
            if (excNames == null || !excNames.contains(fieldK)) {
                if (f.isFlat()) {
                    fieldK = null;
                }

                if (f.isFlat() || o.contains(fieldK)) {
                    Class fieldT = f.type;
                    Type fieldGt = f.genericType;
                    if (f.readonly) {
                        this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst, useGetter), genericInfo);
                    } else {
                        Object val = this.analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst, useGetter), genericInfo);
                        if (val == null && ctx.options.hasFeature(Feature.StringFieldInitEmpty) && f.type == String.class) {
                            val = "";
                        }

                        f.setValue(rst, val, useSetter);
                    }
                }

            }
        }
    }

    private Object analyseBeanOfValue(String fieldK, Class fieldT, Type fieldGt, Context ctx, ONode o, Object rst, Map<String, Type> genericInfo) throws Exception {
        if (genericInfo != null) {
            if (fieldGt instanceof TypeVariable) {
                Type tmp = (Type) genericInfo.get(((Type) fieldGt).getTypeName());
                if (tmp != null) {
                    fieldGt = tmp;
                    if (tmp instanceof Class) {
                        fieldT = (Class) tmp;
                    }
                }
            }

            if (fieldGt instanceof ParameterizedType) {
                ParameterizedType fieldGt2 = (ParameterizedType) fieldGt;
                Type[] actualTypes = fieldGt2.getActualTypeArguments();
                boolean actualTypesChanged = false;
                fieldT = (Class) fieldGt2.getRawType();
                int i = 0;

                for (int len = actualTypes.length; i < len; ++i) {
                    Type tmp = actualTypes[i];
                    if (tmp instanceof TypeVariable) {
                        tmp = (Type) genericInfo.get(tmp.getTypeName());
                        if (tmp != null) {
                            actualTypes[i] = tmp;
                            actualTypesChanged = true;
                        }
                    }
                }

                if (actualTypesChanged) {
                    fieldGt = new ParameterizedTypeImpl((Class) fieldGt2.getRawType(), actualTypes, fieldGt2.getOwnerType());
                }
            }
        }

        return fieldK == null ? this.analyse(ctx, o, rst, fieldT, (Type) fieldGt, genericInfo) : this.analyse(ctx, o.get(fieldK), rst, fieldT, (Type) fieldGt, genericInfo);
    }

    private Class<?> getTypeByNode(Context ctx, AtomicReference<ONode> oRef, Class<?> def) {
        Class<?> clz0 = this.getTypeByNode0(ctx, oRef, def);
        if (Throwable.class.isAssignableFrom(clz0)) {
            return clz0;
        } else {
            return def != null && def != Object.class && !def.isInterface() && !Modifier.isAbstract(def.getModifiers()) ? def : clz0;
        }
    }

    private Class<?> getTypeByNode0(Context ctx, AtomicReference<ONode> oRef, Class<?> def) {
        ONode o = (ONode) oRef.get();
        if (ctx.target_type == null) {
            if (o.isObject()) {
                return LinkedHashMap.class;
            }

            if (o.isArray()) {
                return ArrayList.class;
            }
        }

        String typeStr = null;
        if (!ctx.options.hasFeature(Feature.DisableClassNameRead)) {
            ONode n1;
            if (o.isArray() && o.ary().size() == 2) {
                n1 = (ONode) o.ary().get(0);
                if (n1.isObject() && n1.obj().size() == 1) {
                    ONode n2 = (ONode) n1.obj().get(ctx.options.getTypePropertyName());
                    if (n2 != null) {
                        typeStr = n2.val().getString();
                        ONode o2 = (ONode) o.ary().get(1);
                        oRef.set(o2);
                    }
                }
            }

            if (o.isObject()) {
                n1 = (ONode) o.obj().get(ctx.options.getTypePropertyName());
                if (n1 != null) {
                    typeStr = n1.val().getString();
                }
            }
        }

        if (!StringUtil.isEmpty(typeStr)) {
            if (!typeStr.startsWith("sun.") && !typeStr.startsWith("com.sun.") && !typeStr.startsWith("javax.") && !typeStr.startsWith("jdk.")) {
                Class<?> clz = ctx.options.loadClass(typeStr);
                if (clz == null) {
                    throw new YourSocketException("Unsupported type, class: " + typeStr);
                } else {
                    return clz;
                }
            } else {
                throw new YourSocketException("Unsupported type, class: " + typeStr);
            }
        } else {
            if (def == null || def == Object.class) {
                if (o.isObject()) {
                    return LinkedHashMap.class;
                }

                if (o.isArray()) {
                    return ArrayList.class;
                }
            }

            return def;
        }
    }
}
