//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class GenericUtil {
    private static final Map<Type, Map<String, Type>> genericInfoCached = new HashMap();

    public GenericUtil() {
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
