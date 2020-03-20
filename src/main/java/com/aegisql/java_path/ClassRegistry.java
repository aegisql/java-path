package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class ClassRegistry {

    private final static Map<String, Function<String,?>> CONVERSION_MAP = new HashMap<>();
    private final static Map<String,Class<?>> CLASS_MAP = new HashMap<>();

    final static Function<String,?> voidConstructorSupplier = type->{
        if(CLASS_MAP.containsKey(type)) {
            try {
                Constructor<?> constructor = CLASS_MAP.get(type).getConstructor(null);
                constructor.setAccessible(true);
                return constructor.newInstance(null);
            } catch (Exception e) {
                throw new JavaPathRuntimeException(e);
            }
        }
        try {
            Class<?> aClass = Class.forName(type);
            CLASS_MAP.put(type,aClass);
            return aClass.getConstructor(null).newInstance(null);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    };

    final static Function<String,Function<?,?>> defaultSupplier = alias->{
        return type->{
            if(CLASS_MAP.containsKey(type)) {
                try {
                    Constructor<?> constructor = CLASS_MAP.get(type).getConstructor(null);
                    constructor.setAccessible(true);
                    return constructor.newInstance(null);
                } catch (Exception e) {
                    throw new JavaPathRuntimeException(e);
                }
            }
            if(CLASS_MAP.containsKey(alias)) {
                try {
                    Constructor<?> constructor = CLASS_MAP.get(alias).getConstructor(null);
                    constructor.setAccessible(true);
                    return constructor.newInstance(null);
                } catch (Exception e) {
                    throw new JavaPathRuntimeException(e);
                }
            }
            try {
                return Class.forName(alias).getConstructor(null).newInstance(null);
            } catch (Exception e) {
                throw new JavaPathRuntimeException(e);
            }
        };
    };
    private static Object fromConstructor(Class<?> aClass, String val) {
        try {
            Constructor<?> constructor = aClass.getConstructor(String.class);
            return constructor.newInstance(val);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private static Object fromValueOf(Class<?> aClass, String val) {
        try {
            Method valueOf = aClass.getMethod("valueOf", String.class);
            return valueOf.invoke(null,val);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    static {
        CLASS_MAP.put("s",String.class);
        CLASS_MAP.put("str",String.class);
        CLASS_MAP.put("string",String.class);
        CLASS_MAP.put("String",String.class);

        CLASS_MAP.put("i",int.class);
        CLASS_MAP.put("int",int.class);
        CLASS_MAP.put("integer",int.class);
        CLASS_MAP.put("I",Integer.class);
        CLASS_MAP.put("Int",Integer.class);
        CLASS_MAP.put("Integer",Integer.class);

        CLASS_MAP.put("l",long.class);
        CLASS_MAP.put("long",long.class);
        CLASS_MAP.put("L",Long.class);
        CLASS_MAP.put("Long",Long.class);

        CLASS_MAP.put("b",byte.class);
        CLASS_MAP.put("byte",byte.class);
        CLASS_MAP.put("B",Byte.class);
        CLASS_MAP.put("Byte",Byte.class);

        CLASS_MAP.put("bool",boolean.class);
        CLASS_MAP.put("boolean",boolean.class);
        CLASS_MAP.put("Bool",Boolean.class);
        CLASS_MAP.put("Boolean",Boolean.class);

        CLASS_MAP.put("c",char.class);
        CLASS_MAP.put("ch",char.class);
        CLASS_MAP.put("char",char.class);
        CLASS_MAP.put("C",Character.class);
        CLASS_MAP.put("Ch",Character.class);
        CLASS_MAP.put("Char",Character.class);

        CLASS_MAP.put("d",double.class);
        CLASS_MAP.put("double",double.class);
        CLASS_MAP.put("D",Double.class);
        CLASS_MAP.put("Double",Double.class);

        CLASS_MAP.put("f",float.class);
        CLASS_MAP.put("float",float.class);
        CLASS_MAP.put("F",Float.class);
        CLASS_MAP.put("Float",Float.class);

        CLASS_MAP.put("new",Function.class);
        CLASS_MAP.put("key->new",Function.class);

        CLASS_MAP.put("list", ArrayList.class);
        CLASS_MAP.put("map", HashMap.class);
        CLASS_MAP.put("set", HashSet.class);

        CONVERSION_MAP.put(String.class.getName(),str->fromConstructor(String.class,str));
        CONVERSION_MAP.put(Character.class.getName(),str->fromValueOf(Character.class,str));
        CONVERSION_MAP.put(Integer.class.getName(),str->fromValueOf(Integer.class,str));
        CONVERSION_MAP.put(Long.class.getName(),str->fromValueOf(Long.class,str));
        CONVERSION_MAP.put(Byte.class.getName(),str->fromValueOf(Byte.class,str));
        CONVERSION_MAP.put(Double.class.getName(),str->fromValueOf(Double.class,str));
        CONVERSION_MAP.put(Float.class.getName(),str->fromValueOf(Float.class,str));
        CONVERSION_MAP.put(Boolean.class.getName(),str->fromValueOf(Boolean.class,str));

        CONVERSION_MAP.put(char.class.getName(),str->fromValueOf(Character.class,str));
        CONVERSION_MAP.put(int.class.getName(),str->fromValueOf(Integer.class,str));
        CONVERSION_MAP.put(long.class.getName(),str->fromValueOf(Long.class,str));
        CONVERSION_MAP.put(byte.class.getName(),str->fromValueOf(Byte.class,str));
        CONVERSION_MAP.put(double.class.getName(),str->fromValueOf(Double.class,str));
        CONVERSION_MAP.put(float.class.getName(),str->fromValueOf(Float.class,str));
        CONVERSION_MAP.put(boolean.class.getName(),str->fromValueOf(Boolean.class,str));

        CONVERSION_MAP.put("key->new",defaultSupplier);
        CONVERSION_MAP.put("new",voidConstructorSupplier);
    }

    final Map<String, Function<String,?>> conversionMap = new HashMap<>();
    final Map<String,Class<?>> classMap = new HashMap<>();

    public ClassRegistry() {
        this.classMap.putAll(CLASS_MAP);
        this.conversionMap.putAll(CONVERSION_MAP);
    }

    public void registerClassShortName(Class<?> aClass, String shortName) {
        Objects.requireNonNull(shortName,"registerClassShortName requires non empty name");
        Objects.requireNonNull(aClass,"registerClassShortName requires non empty class");
        if(classMap.containsKey(shortName) && ! aClass.equals(classMap.get(shortName))) {
            throw new JavaPathRuntimeException("Short name "+shortName+" for class "+aClass.getSimpleName()+" already occupied by "+classMap.get(shortName).getSimpleName());
        }
        classMap.put(shortName,aClass);
    }

    public void registerClassSimpleName(Class<?> aClass) {
        registerClassShortName(aClass,aClass.getSimpleName());
    }

    public <T> void registerStringConverter(Class<T> aClass, Function<String,T> converter) {
        Objects.requireNonNull(aClass,"registerStringConverter requires non empty class");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+aClass.getSimpleName());
        conversionMap.put(aClass.getName(),converter);
    }

    public <T> void registerStringConverter(String alias, Function<String,T> converter) {
        Objects.requireNonNull(alias,"registerStringConverter requires non empty class alias");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+alias);
        conversionMap.put(alias,converter);
    }

}
