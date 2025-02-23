
package org.yourmq.base;

import org.yourmq.exception.YourSocketException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EnumWrap {
    protected final Map<String, Enum> enumMap = new HashMap();
    protected final Map<String, Enum> enumCustomMap = new HashMap();
    protected final Enum[] enumOrdinal;
    protected final Class<?> enumClass;
    protected Field enumCustomFiled;

    public Class<?> enumClass() {
        return this.enumClass;
    }

    public EnumWrap(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.enumOrdinal = (Enum[]) ((Enum[]) enumClass.getEnumConstants());
        if (null != this.enumOrdinal) {
            for (int i = 0; i < this.enumOrdinal.length; ++i) {
                Enum e = this.enumOrdinal[i];
                if (!this.enumMap.containsKey(e.name().toLowerCase())) {
                    this.enumMap.put(e.name().toLowerCase(), e);
                    Field[] var4 = e.getClass().getDeclaredFields();
                    int var5 = var4.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        Field field = var4[var6];
                        if (field.isAnnotationPresent(ONodeAttr.class)) {
                            field.setAccessible(true);

                            try {
                                Object custom = field.get(e);
                                this.enumCustomFiled = field;
                                this.enumCustomMap.put(enumClass.getName() + "#" + custom, e);
                            } catch (IllegalAccessException var9) {
                                throw new YourSocketException(var9);
                            }
                        }
                    }
                }
            }

        }
    }

    public Enum get(int ordinal) {
        return this.enumOrdinal[ordinal];
    }

    public Enum get(String name) {
        return (Enum) this.enumMap.get(name.toLowerCase());
    }

    public Enum getCustom(String custom) {
        return (Enum) this.enumCustomMap.get(this.enumClass.getName() + "#" + custom);
    }

    public boolean hasCustom() {
        return this.enumCustomMap.size() > 0;
    }

    public Object getCustomValue(Object o) {
        try {
            return this.enumCustomFiled == null ? null : this.enumCustomFiled.get(o);
        } catch (IllegalAccessException var3) {
            throw new YourSocketException(var3);
        }
    }
}
