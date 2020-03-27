package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

@FunctionalInterface
public interface StringConverter<T> extends Function<String,T> {

    static StringConverter<String> identity() {
        return str->str;
    }

    static <X> StringConverter<X> factory(Class<X> aClass, String methodName) {
        final Method valueOf;
        try {
            valueOf = aClass.getMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
        return str->{
            try {
                valueOf.setAccessible(true);
                return (X)valueOf.invoke(null,str);
            } catch (Exception e) {
                throw new JavaPathRuntimeException(e);
            }
        };
    }

    static <X> StringConverter<X> valueOf(Class<X> aClass) {
        return factory(aClass,"valueOf");
    }

    static <X> StringConverter<X> constructor(Class<X> aClass) {
        final Constructor<?> constructor;
        try {
            constructor = aClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
        return str->{
            try {
                constructor.setAccessible(true);
                return (X)constructor.newInstance(str);
            } catch (Exception e) {
                throw new JavaPathRuntimeException(e);
            }
        };
    }

    }
