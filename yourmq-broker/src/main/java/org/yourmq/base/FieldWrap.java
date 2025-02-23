
package org.yourmq.base;

import org.yourmq.exception.YourSocketException;
import org.yourmq.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.util.TimeZone;

public class FieldWrap {
    public final Field field;
    public final Class<?> type;
    public final Type genericType;
    public final boolean readonly;
    public final boolean hasSetter;
    public final boolean hasGetter;
    private String name;
    private String format;
    private TimeZone timeZone;
    private boolean asString = false;
    private boolean serialize = true;
    private boolean deserialize = true;
    private boolean incNull = true;
    private boolean flat = false;
    private Method _setter;
    private Method _getter;

    public FieldWrap(Class<?> clz, Field f, boolean isFinal) {
        this.field = f;
        this.type = f.getType();
        this.genericType = f.getGenericType();
        this.readonly = isFinal;
        NodeName anno = (NodeName) f.getAnnotation(NodeName.class);
        if (anno != null) {
            this.name = anno.value();
        }

        ONodeAttr attr = (ONodeAttr) f.getAnnotation(ONodeAttr.class);
        if (attr != null) {
            this.name = attr.name();
            this.format = attr.format();
            this.incNull = attr.incNull();
            this.flat = attr.flat();
            this.asString = attr.asString();
            if (!StringUtil.isEmpty(attr.timezone())) {
                this.timeZone = TimeZone.getTimeZone(ZoneId.of(attr.timezone()));
            }

            if (attr.ignore()) {
                this.serialize = false;
                this.deserialize = false;
            } else {
                this.serialize = attr.serialize();
                this.deserialize = attr.deserialize();
            }
        }

        if (StringUtil.isEmpty(this.name)) {
            this.name = this.field.getName();
        }

        this._setter = doFindSetter(clz, f);
        this._getter = doFindGetter(clz, f);
        this.hasSetter = this._setter != null;
        this.hasGetter = this._getter != null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String name() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getFormat() {
        return this.format;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public boolean isDeserialize() {
        return this.deserialize;
    }

    public boolean isSerialize() {
        return this.serialize;
    }

    public boolean isIncNull() {
        return this.incNull;
    }

    public boolean isFlat() {
        return this.flat;
    }

    public boolean isAsString() {
        return this.asString;
    }

    public void setValue(Object tObj, Object val) {
        this.setValue(tObj, val, false);
    }

    public void setValue(Object tObj, Object val, boolean useSetter) {
        if (!this.readonly) {
            try {
                if (this._setter != null && useSetter) {
                    this._setter.invoke(tObj, val);
                } else {
                    if (!this.field.isAccessible()) {
                        this.field.setAccessible(true);
                    }

                    this.field.set(tObj, val);
                }

            } catch (IllegalArgumentException var5) {
                if (val == null) {
                    throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure!", var5);
                } else {
                    throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure ：val(" + val.getClass().getSimpleName() + ")", var5);
                }
            } catch (IllegalAccessException var6) {
                throw new YourSocketException(var6);
            } catch (RuntimeException var7) {
                throw var7;
            } catch (Throwable var8) {
                throw new RuntimeException(var8);
            }
        }
    }

    public Object getValue(Object tObj, boolean useGetter) {
        try {
            if (this._getter != null && useGetter) {
                return this._getter.invoke(tObj);
            } else {
                if (!this.field.isAccessible()) {
                    this.field.setAccessible(true);
                }

                return this.field.get(tObj);
            }
        } catch (IllegalAccessException var4) {
            throw new YourSocketException(var4);
        } catch (RuntimeException var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new RuntimeException(var6);
        }
    }

    private static Method doFindSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, field.getType());
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException var6) {
        } catch (RuntimeException var7) {
            throw var7;
        } catch (Throwable var8) {
            throw new RuntimeException(var8);
        }

        return null;
    }

    private static Method doFindGetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if (getFun != null) {
                return getFun;
            }
        } catch (NoSuchMethodException var6) {
        } catch (RuntimeException var7) {
            throw var7;
        } catch (Throwable var8) {
            throw new RuntimeException(var8);
        }

        return null;
    }
}
