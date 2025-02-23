
package org.yourmq.base;


import org.yourmq.exception.YourSocketException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Context {
    public final Options options;
    public Object source;
    public int depth = 0;
    public boolean pretty = false;
    public Object target;
    public Class<?> target_clz;
    public Type target_type;

    public Context(Options options, Object from) {
        this.options = options;
        this.source = from;
    }

    public Context(Options options, ONode node, Type type0) {
        this.options = options;
        this.source = node;
        if (type0 != null) {
            if (type0 instanceof Class) {
                Class<?> clz = (Class) type0;
                if (TypeRef.class.isAssignableFrom(clz)) {
                    Type superClass = clz.getGenericSuperclass();
                    Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
                    this.initType(type);
                    return;
                }

                if (clz.getName().indexOf("$") > 0) {
                    if (clz.isMemberClass()) {
                        this.initType(clz, clz);
                    } else {
                        this.initType(clz.getGenericSuperclass());
                    }
                } else {
                    this.initType(clz, clz);
                }
            } else {
                this.initType(type0);
            }

        }
    }

    private void initType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            this.initType(type, (Class) pType.getRawType());
        } else {
            this.initType(type, (Class) type);
        }

    }

    private void initType(Type type, Class<?> clz) {
        this.target_type = type;
        this.target_clz = clz;
        if (clz != null && clz != Object.class && this.options.getClassLoader() == null) {
            this.options.setClassLoader(clz.getClassLoader());
        }

    }

    public Context handle(Handler handler) {
        try {
            handler.handle(this);
            return this;
        } catch (RuntimeException var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new YourSocketException(var4);
        }
    }
}
