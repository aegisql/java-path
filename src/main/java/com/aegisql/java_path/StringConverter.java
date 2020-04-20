package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * The interface String converter.
 * Converts String to any object that can be derived from it
 * Example: "10" -> int 10
 *
 * @param <T> the type parameter
 */
@FunctionalInterface
public interface StringConverter<T> extends Function<String,T> {

    /**
     * Identity string converter returns passed parameter without modification
     *
     * @return the string converter
     */
    static StringConverter<String> identity() {
        return str->str;
    }

    /**
     * Creates new object using a static factory method with a single String parameter
     *
     * @param <X>        the type parameter
     * @param aClass     the a class
     * @param methodName the method name
     * @return the string converter
     */
    static <X> StringConverter<X> factory(Class<X> aClass, String methodName) {
        final Method valueOf;
        try {
            valueOf = aClass.getMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
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

    /**
     * Creates new object using static valueOf(String s) method
     * Many Java classes implement it, such as Integer, all enum's
     *
     * @param <X>    the type parameter
     * @param aClass the a class
     * @return the string converter
     */
    static <X> StringConverter<X> valueOf(Class<X> aClass) {
        return factory(aClass,"valueOf");
    }

    /**
     * Creates new object using constructor with a single String parameter.
     *
     * @param <X>    the type parameter
     * @param aClass the a class
     * @return the string converter
     */
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
