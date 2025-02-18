//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ClassWrap {
    private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap();
    private final Class<?> _clz;
    private final Map<String, FieldWrap> _fieldAllWraps;
    private final Map<String, Method> _propertyAll;
    private boolean _recordable;
    private Constructor _recordConstructor;
    private Parameter[] _recordParams;
    private boolean _isMemberClass;

    public static ClassWrap get(Class<?> clz) {
        ClassWrap cw = (ClassWrap) cached.get(clz);
        if (cw == null) {
            cw = new ClassWrap(clz);
            ClassWrap l = (ClassWrap) cached.putIfAbsent(clz, cw);
            if (l != null) {
                cw = l;
            }
        }

        return cw;
    }

    protected ClassWrap(Class<?> clz) {
        this._clz = clz;
        this._recordable = true;
        this._isMemberClass = clz.isMemberClass();
        this._fieldAllWraps = new LinkedHashMap();
        this._propertyAll = new LinkedHashMap();
        Predicate var10002 = this._fieldAllWraps::containsKey;
        Map var10003 = this._fieldAllWraps;
        this.scanAllFields(clz, var10002, var10003::put);
        Method[] var2 = clz.getMethods();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Method m = var2[var4];
            if (m.getName().startsWith("set") && m.getName().length() > 3 && m.getParameterCount() == 1) {
                String name = m.getName().substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                this._propertyAll.put(name, m);
            }
        }

        if (this._fieldAllWraps.size() == 0) {
            this._recordable = false;
        }

        Constructor<?>[] constructors = clz.getConstructors();
        if (constructors.length > 0) {
            if (this._recordable) {
                this._recordConstructor = constructors[constructors.length - 1];
                this._recordParams = this._recordConstructor.getParameters();
                if (this._recordParams.length == 0) {
                    this._recordable = false;
                }
            } else if (constructors.length == 1 && constructors[0].getParameterCount() > 0) {
                this._recordConstructor = constructors[0];
                this._recordParams = this._recordConstructor.getParameters();
            }
        } else {
            this._recordable = false;
        }

    }

    public Class<?> clz() {
        return this._clz;
    }

    public Collection<FieldWrap> fieldAllWraps() {
        return this._fieldAllWraps.values();
    }

    public FieldWrap getFieldWrap(String fieldName) {
        return (FieldWrap) this._fieldAllWraps.get(fieldName);
    }

    public Method getProperty(String name) {
        return (Method) this._propertyAll.get(name);
    }

    public boolean recordable() {
        return this._recordable;
    }

    public Constructor recordConstructor() {
        return this._recordConstructor;
    }

    public Parameter[] recordParams() {
        return this._recordParams;
    }

    private void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
        if (clz != null) {
            Field[] var4 = clz.getDeclaredFields();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Field f = var4[var6];
                int mod = f.getModifiers();
                if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod) && (!this._isMemberClass || !f.getName().equals("this$0")) && !checker.test(f.getName())) {
                    this._recordable &= Modifier.isFinal(mod);
                    consumer.accept(f.getName(), new FieldWrap(clz, f, Modifier.isFinal(mod)));
                }
            }

            Class<?> sup = clz.getSuperclass();
            if (sup != Object.class) {
                this.scanAllFields(sup, checker, consumer);
            }

        }
    }
}
