
package org.yourmq.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class GenericUtil {
    private static final Map<Type, Map<String, Type>> genericInfoCached = new HashMap();

    public GenericUtil() {
    }

    private static List<Class<?>> getGenericInterfaces(Class<?> clazz) {
        return getGenericInterfaces(clazz, new ArrayList());
    }

    private static List<Class<?>> getGenericInterfaces(Class<?> clazz, List<Class<?>> classes) {
        Type[] var2 = clazz.getGenericInterfaces();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Type supIfc = var2[var4];
            if (supIfc instanceof ParameterizedType) {
                Class<?> rawClz = (Class) ((ParameterizedType) supIfc).getRawType();
                classes.add(rawClz);
                getGenericInterfaces(rawClz, classes);
            }
        }

        return classes;
    }

    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        Type[] var2 = clazz.getGenericInterfaces();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Type supIfc = var2[var4];
            if (supIfc instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) supIfc;
                Class<?> rawClz = (Class) type.getRawType();
                if (rawClz == genericIfc || getGenericInterfaces(rawClz).contains(genericIfc)) {
                    return (Class[]) Arrays.stream(type.getActualTypeArguments()).filter((item) -> {
                        return item instanceof Class;
                    }).map((item) -> {
                        return (Class) item;
                    }).toArray((x$0) -> {
                        return new Class[x$0];
                    });
                }
            } else if (supIfc instanceof Class) {
                Class<?>[] classes = resolveTypeArguments((Class) supIfc, genericIfc);
                if (classes != null) {
                    return classes;
                }
            }
        }

        Type supClz = clazz.getGenericSuperclass();
        if (supClz instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) supClz;
            return (Class[]) Arrays.stream(type.getActualTypeArguments()).filter((item) -> {
                return item instanceof Class;
            }).map((item) -> {
                return (Class) item;
            }).toArray((x$0) -> {
                return new Class[x$0];
            });
        } else {
            return null;
        }
    }

    public static Map<String, Type> getGenericInfo(Type type) {
        Map<String, Type> tmp = (Map) genericInfoCached.get(type);
        if (tmp == null) {
            synchronized (type) {
                tmp = (Map) genericInfoCached.get(type);
                if (tmp == null) {
                    tmp = createTypeGenericMap(type);
                    genericInfoCached.put(type, tmp);
                }
            }
        }

        return tmp;
    }

    private static Map<String, Type> createTypeGenericMap(Type type) {
        HashMap typeMap;
        Class rawType;
        for (typeMap = new HashMap(); null != type; type = rawType) {
            ParameterizedType parameterizedType = toParameterizedType((Type) type);
            if (null == parameterizedType) {
                break;
            }

            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            rawType = (Class) parameterizedType.getRawType();
            TypeVariable[] typeParameters = rawType.getTypeParameters();

            for (int i = 0; i < typeParameters.length; ++i) {
                Type value = typeArguments[i];
                if (!(value instanceof TypeVariable)) {
                    typeMap.put(typeParameters[i].getTypeName(), value);
                }
            }
        }

        return typeMap;
    }

    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (genericInterfaces != null && genericInterfaces.length > 0) {
                    genericSuper = genericInterfaces[0];
                }
            }

            result = toParameterizedType(genericSuper);
        }

        return result;
    }
}
