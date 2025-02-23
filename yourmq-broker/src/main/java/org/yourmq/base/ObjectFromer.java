
package org.yourmq.base;

import org.yourmq.common.DEFAULTS;
import org.yourmq.common.Feature;
import org.yourmq.utils.BeanUtil;
import org.yourmq.utils.DateUtil;
import org.yourmq.utils.StringUtil;
import org.yourmq.utils.TypeUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ObjectFromer implements Fromer {
    public ObjectFromer() {
    }

    @Override
    public void handle(Context ctx) {
        ctx.target = this.analyse(ctx.options, ctx.source);
    }

    private ONode analyse(Options opt, Object source) {
        ONode rst = new ONode((ONode) null, opt);
        if (source == null) {
            return rst;
        } else {
            Class<?> clz = source.getClass();
            Iterator var5 = opt.encoders().iterator();

            while (var5.hasNext()) {
                NodeEncoderEntity encoder = (NodeEncoderEntity) var5.next();
                if (encoder.isEncodable(clz)) {
                    encoder.encode(source, rst);
                    return rst;
                }
            }

            if (source instanceof ONode) {
                rst.val(source);
            } else {
                String clzName;
                if (source instanceof String) {
                    if (opt.hasFeature(Feature.StringJsonToNode)) {
                        clzName = (String) source;
                        ONode otmp = null;
                        if (clzName.startsWith("{") && clzName.endsWith("}") || clzName.startsWith("[") && clzName.endsWith("]")) {
                            otmp = ONode.loadStr(clzName, opt);
                        }

                        if (otmp == null) {
                            rst.val().setString(clzName);
                        } else {
                            rst.val(otmp);
                        }
                    } else {
                        rst.val().setString((String) source);
                    }
                } else if (source instanceof UUID) {
                    rst.val().setString(((UUID) source).toString());
                } else if (source instanceof Date) {
                    rst.val().setDate((Date) source);
                } else if (source instanceof ZonedDateTime) {
                    rst.val().setDate(Date.from(((ZonedDateTime) source).toInstant()));
                } else if (source instanceof OffsetDateTime) {
                    rst.val().setDate(Date.from(((OffsetDateTime) source).toInstant()));
                } else {
                    Instant instant;
                    if (source instanceof LocalDateTime) {
                        instant = ((LocalDateTime) source).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
                        rst.val().setDate(new Date(instant.getEpochSecond() * 1000L + (long) (instant.getNano() / 1000000)));
                    } else if (source instanceof LocalDate) {
                        instant = ((LocalDate) source).atTime(LocalTime.MIN).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
                        rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
                    } else if (source instanceof LocalTime) {
                        instant = ((LocalTime) source).atDate(LocalDate.of(1970, 1, 1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
                        rst.val().setDate(new Date(instant.getEpochSecond() * 1000L));
                    } else if (source instanceof OffsetTime) {
                        instant = ((OffsetTime) source).atDate(LocalDate.of(1970, 1, 1)).toInstant();
                        rst.val().setDate(Date.from(instant));
                    } else if (source instanceof Boolean) {
                        rst.val().setBool((Boolean) source);
                    } else if (source instanceof Number) {
                        rst.val().setNumber((Number) source);
                    } else if (source instanceof Throwable) {
                        this.analyseBean(opt, rst, clz, source);
                    } else if (source instanceof Properties) {
                        this.analyseProps(opt, rst, clz, source);
                    } else if (source instanceof NameValues) {
                        this.analyseNameValues(opt, rst, clz, source);
                    } else if (!this.analyseArray(opt, rst, clz, source)) {
                        Object k;
                        if (!clz.isEnum() && !Enum.class.isAssignableFrom(clz)) {
                            Iterator var16;
                            if (source instanceof Map) {
                                if (opt.hasFeature(Feature.WriteClassName)) {
                                    this.typeSet(opt, rst, clz);
                                }

                                rst.asObject();
                                Map map = (Map) source;
                                var16 = map.keySet().iterator();

                                while (true) {
                                    Object v;
                                    do {
                                        do {
                                            if (!var16.hasNext()) {
                                                return rst;
                                            }

                                            k = var16.next();
                                        } while (k == null);

                                        v = map.get(k);
                                    } while (v == null && !opt.hasFeature(Feature.SerializeNulls) && !opt.hasFeature(Feature.SerializeMapNullValues));

                                    rst.setNode(k.toString(), this.analyse(opt, v));
                                }
                            } else if (source instanceof Iterable) {
                                rst.asArray();
                                ONode ary = rst;
                                if (opt.hasFeature(Feature.WriteArrayClassName)) {
                                    rst.add(this.typeSet(opt, new ONode((ONode) null, opt), clz));
                                    ary = rst.addNew().asArray();
                                }

                                var16 = ((Iterable) source).iterator();

                                while (var16.hasNext()) {
                                    k = var16.next();
                                    ary.addNode(this.analyse(opt, k));
                                }
                            } else if (source instanceof Enumeration) {
                                rst.asArray();
                                Enumeration o = (Enumeration) source;

                                while (o.hasMoreElements()) {
                                    rst.addNode(this.analyse(opt, o.nextElement()));
                                }
                            } else {
                                clzName = clz.getName();
                                if (clzName.endsWith(".Undefined")) {
                                    rst.val().setNull();
                                } else if (!this.analyseOther(opt, rst, clz, source) && !clzName.startsWith("jdk.")) {
                                    this.analyseBean(opt, rst, clz, source);
                                }
                            }
                        } else {
                            Enum em = (Enum) source;
                            EnumWrap ew = TypeUtil.createEnum(source.getClass());
                            k = ew.getCustomValue(em);
                            if (k != null) {
                                rst.val().set(k);
                            } else if (opt.hasFeature(Feature.EnumUsingName)) {
                                rst.val().setString(em.name());
                            } else {
                                rst.val().setNumber(em.ordinal());
                            }
                        }
                    }
                }
            }

            return rst;
        }
    }

    private ONode typeSet(Options cfg, ONode o, Class<?> clz) {
        return o.set(cfg.getTypePropertyName(), clz.getName());
    }

    private boolean analyseArray(Options cfg, ONode rst, Class<?> clz, Object obj) {
        int var6;
        int var7;
        if (obj instanceof Object[]) {
            rst.asArray();
            Object[] var5 = (Object[]) ((Object[]) obj);
            var6 = var5.length;

            for (var7 = 0; var7 < var6; ++var7) {
                Object o = var5[var7];
                rst.addNode(this.analyse(cfg, o));
            }
        } else {
            int o;
            if (obj instanceof byte[]) {
                rst.asArray();
                byte[] var10 = (byte[]) ((byte[]) obj);
                var6 = var10.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    o = var10[var7];
                    rst.addNode(this.analyse(cfg, Byte.valueOf((byte) o)));
                }
            } else if (obj instanceof short[]) {
                rst.asArray();
                short[] var11 = (short[]) ((short[]) obj);
                var6 = var11.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    o = var11[var7];
                    rst.addNode(this.analyse(cfg, Short.valueOf((short) o)));
                }
            } else if (obj instanceof int[]) {
                rst.asArray();
                int[] var12 = (int[]) ((int[]) obj);
                var6 = var12.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    o = var12[var7];
                    rst.addNode(this.analyse(cfg, o));
                }
            } else if (obj instanceof long[]) {
                rst.asArray();
                long[] var13 = (long[]) ((long[]) obj);
                var6 = var13.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    long o2 = var13[var7];
                    rst.addNode(this.analyse(cfg, o2));
                }
            } else if (obj instanceof float[]) {
                rst.asArray();
                float[] var14 = (float[]) ((float[]) obj);
                var6 = var14.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    float o3 = var14[var7];
                    rst.addNode(this.analyse(cfg, o3));
                }
            } else if (obj instanceof double[]) {
                rst.asArray();
                double[] var15 = (double[]) ((double[]) obj);
                var6 = var15.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    double o4 = var15[var7];
                    rst.addNode(this.analyse(cfg, o4));
                }
            } else if (obj instanceof boolean[]) {
                rst.asArray();
                boolean[] var16 = (boolean[]) ((boolean[]) obj);
                var6 = var16.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    boolean o5 = var16[var7];
                    rst.addNode(this.analyse(cfg, o5));
                }
            } else {
                if (!(obj instanceof char[])) {
                    return false;
                }

                rst.asArray();
                char[] var17 = (char[]) ((char[]) obj);
                var6 = var17.length;

                for (var7 = 0; var7 < var6; ++var7) {
                    char o6 = var17[var7];
                    rst.addNode(this.analyse(cfg, o6));
                }
            }
        }

        return true;
    }

    private boolean analyseProps(Options cfg, ONode rst, Class<?> clz, Object obj) {
        Properties props = (Properties) obj;
        if (props.size() == 0) {
            rst.asNull();
            return true;
        } else {
            List<String> keyVector = new ArrayList();
            props.keySet().forEach((k) -> {
                if (k instanceof String) {
                    keyVector.add((String) k);
                }

            });
            Collections.sort(keyVector);
            if (((String) keyVector.get(0)).startsWith("[")) {
                rst.asArray();
            } else {
                rst.asObject();
            }

            Iterator var7 = keyVector.iterator();

            while (var7.hasNext()) {
                String key = (String) var7.next();
                String val = props.getProperty(key);
                this.analysePropsItem(rst, key, val);
            }

            return true;
        }
    }

    private boolean analyseNameValues(Options cfg, ONode rst, Class<?> clz, Object obj) {
        NameValues props = (NameValues) obj;
        if (props.size() == 0) {
            rst.asNull();
            return true;
        } else {
            props.sort();
            if (((String) props.get(0).getKey()).startsWith("[")) {
                rst.asArray();
            } else {
                rst.asObject();
            }

            Iterator var6 = props.getItems().iterator();

            while (var6.hasNext()) {
                Map.Entry<String, String> kv = (Map.Entry) var6.next();
                String key = (String) kv.getKey();
                String val = (String) kv.getValue();
                this.analysePropsItem(rst, key, val);
            }

            return true;
        }
    }

    private void analysePropsItem(ONode rst, String key, String val) {
        String[] keySegments = key.split("\\.");
        ONode n1 = rst;

        for (int i = 0; i < keySegments.length; ++i) {
            String p1 = keySegments[i];
            if (!p1.endsWith("]")) {
                n1 = n1.getOrNew(p1);
            } else {
                String tmp = p1.substring(p1.lastIndexOf(91) + 1, p1.length() - 1);
                p1 = p1.substring(0, p1.lastIndexOf(91));
                if (tmp.length() > 0) {
                    if (StringUtil.isInteger(tmp)) {
                        int idx = Integer.parseInt(tmp);
                        if (p1.length() > 0) {
                            n1 = n1.getOrNew(p1).getOrNew(idx);
                        } else {
                            n1 = n1.getOrNew(idx);
                        }
                    } else {
                        if (tmp.length() > 2 && (tmp.indexOf(39) == 0 || tmp.indexOf(34) == 0)) {
                            tmp = tmp.substring(1, tmp.length() - 1);
                        }

                        if (p1.length() > 0) {
                            n1 = n1.getOrNew(p1).getOrNew(tmp);
                        } else {
                            n1 = n1.getOrNew(tmp);
                        }
                    }
                } else if (p1.length() > 0) {
                    n1 = n1.getOrNew(p1).addNew();
                } else {
                    n1 = n1.addNew();
                }
            }
        }

        n1.val(val);
    }

    private boolean analyseBean(Options cfg, ONode rst, Class<?> clz, Object obj) {
        rst.asObject();
        if (cfg.hasFeature(Feature.WriteClassName)) {
            rst.set(cfg.getTypePropertyName(), clz.getName());
        }

        Collection<FieldWrap> list = ClassWrap.get(clz).fieldAllWraps();
        boolean useGetter = cfg.hasFeature(Feature.UseGetter);
        boolean useOnlyGetter = cfg.hasFeature(Feature.UseOnlyGetter);
        if (useOnlyGetter) {
            useGetter = true;
        }

        Iterator var8 = list.iterator();

        while (true) {
            while (true) {
                FieldWrap f;
                do {
                    while (true) {
                        do {
                            do {
                                if (!var8.hasNext()) {
                                    return true;
                                }

                                f = (FieldWrap) var8.next();
                            } while (!f.isSerialize());
                        } while (useOnlyGetter && !f.hasGetter);

                        Object val = f.getValue(obj, useGetter);
                        if (val == null) {
                            break;
                        }

                        if (!val.equals(obj)) {
                            if (!StringUtil.isEmpty(f.getFormat())) {
                                if (val instanceof Date) {
                                    String val2 = DateUtil.format((Date) val, f.getFormat(), f.getTimeZone());
                                    rst.set(f.getName(), val2);
                                    continue;
                                }

                                String val2;
                                DateTimeFormatter fmt;
                                if (val instanceof LocalDateTime) {
                                    fmt = DateTimeFormatter.ofPattern(f.getFormat());
                                    if (f.getTimeZone() != null) {
                                        fmt.withZone(f.getTimeZone().toZoneId());
                                    }

                                    val2 = ((LocalDateTime) val).format(fmt);
                                    rst.set(f.getName(), val2);
                                    continue;
                                }

                                if (val instanceof LocalDate) {
                                    fmt = DateTimeFormatter.ofPattern(f.getFormat());
                                    if (f.getTimeZone() != null) {
                                        fmt.withZone(f.getTimeZone().toZoneId());
                                    }

                                    val2 = ((LocalDate) val).format(fmt);
                                    rst.set(f.getName(), val2);
                                    continue;
                                }

                                if (val instanceof LocalTime) {
                                    fmt = DateTimeFormatter.ofPattern(f.getFormat());
                                    if (f.getTimeZone() != null) {
                                        fmt.withZone(f.getTimeZone().toZoneId());
                                    }

                                    val2 = ((LocalTime) val).format(fmt);
                                    rst.set(f.getName(), val2);
                                    continue;
                                }

                                if (val instanceof Number) {
                                    NumberFormat format = new DecimalFormat(f.getFormat());
                                    val2 = format.format(val);
                                    rst.set(f.getName(), val2);
                                    continue;
                                }
                            }

                            if (f.isAsString()) {
                                rst.set(f.getName(), val.toString());
                            } else {
                                ONode analysed = this.analyse(cfg, val);
                                if (f.isFlat() && analysed.isObject()) {
                                    analysed.forEach((key, node) -> {
                                        rst.setNode(key, node);
                                    });
                                } else {
                                    rst.setNode(f.getName(), analysed);
                                }
                            }
                        }
                    }
                } while (!f.isIncNull());

                if (cfg.hasFeature(Feature.StringNullAsEmpty) && f.type == String.class) {
                    rst.setNode(f.getName(), this.analyse(cfg, ""));
                } else if (cfg.hasFeature(Feature.BooleanNullAsFalse) && f.type == Boolean.class) {
                    rst.setNode(f.getName(), this.analyse(cfg, false));
                } else if (cfg.hasFeature(Feature.NumberNullAsZero) && Number.class.isAssignableFrom(f.type)) {
                    if (f.type == Long.class) {
                        rst.setNode(f.getName(), this.analyse(cfg, 0L));
                    } else if (f.type == Double.class) {
                        rst.setNode(f.getName(), this.analyse(cfg, 0.0));
                    } else if (f.type == Float.class) {
                        rst.setNode(f.getName(), this.analyse(cfg, 0.0F));
                    } else {
                        rst.setNode(f.getName(), this.analyse(cfg, 0));
                    }
                } else if (!cfg.hasFeature(Feature.ArrayNullAsEmpty) || !Collection.class.isAssignableFrom(f.type) && !f.type.isArray()) {
                    if (cfg.hasFeature(Feature.SerializeNulls)) {
                        rst.setNode(f.getName(), (new ONode((ONode) null, cfg)).asValue());
                    }
                } else {
                    rst.setNode(f.getName(), (new ONode((ONode) null, cfg)).asArray());
                }
            }
        }
    }

    private boolean analyseOther(Options cfg, ONode rst, Class<?> clz, Object obj) {
        if (obj instanceof SimpleDateFormat) {
            rst.set(cfg.getTypePropertyName(), clz.getName());
            rst.set("val", ((SimpleDateFormat) obj).toPattern());
        } else if (clz == Class.class) {
            rst.val().setString(clz.getName());
        } else if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            InetAddress inetAddress = address.getAddress();
            rst.set("address", inetAddress.getHostAddress());
            rst.set("port", address.getPort());
        } else if (obj instanceof File) {
            rst.val().setString(((File) obj).getPath());
        } else if (obj instanceof InetAddress) {
            rst.val().setString(((InetAddress) obj).getHostAddress());
        } else if (obj instanceof TimeZone) {
            rst.val().setString(((TimeZone) obj).getID());
        } else if (obj instanceof Currency) {
            rst.val().setString(((Currency) obj).getCurrencyCode());
        } else if (obj instanceof Iterator) {
            rst.asArray();
            ((Iterator) obj).forEachRemaining((vx) -> {
                rst.add(this.analyse(cfg, vx));
            });
        } else if (obj instanceof Map.Entry) {
            Map.Entry kv = (Map.Entry) obj;
            Object k = kv.getKey();
            Object v = kv.getValue();
            rst.asObject();
            if (k != null) {
                rst.set(k.toString(), this.analyse(cfg, v));
            }
        } else if (obj instanceof Calendar) {
            rst.val().setDate(((Calendar) obj).getTime());
        } else if (obj instanceof Clob) {
            rst.val().setString(BeanUtil.clobToString((Clob) obj));
        } else {
            if (!(obj instanceof Appendable)) {
                return false;
            }

            rst.val().setString(obj.toString());
        }

        return true;
    }
}
