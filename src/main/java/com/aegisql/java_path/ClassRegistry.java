package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static com.aegisql.java_path.StringConverter.identity;
import static com.aegisql.java_path.StringConverter.valueOf;

public class ClassRegistry {

    private final static Map<String, StringConverter<?>> CONVERSION_MAP = new HashMap<>();
    private final static Map<String,Class<?>> CLASS_MAP = new HashMap<>();

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
        CLASS_MAP.put("void",void.class);
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

        CLASS_MAP.put("short",short.class);
        CLASS_MAP.put("Short",Short.class);

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

        CLASS_MAP.put("ArrayList", ArrayList.class);
        CLASS_MAP.put("LinkedList", LinkedList.class);
        CLASS_MAP.put("HashMap", HashMap.class);
        CLASS_MAP.put("TreeMap", TreeMap.class);
        CLASS_MAP.put("HashSet", HashSet.class);
        CLASS_MAP.put("TreeSet", TreeSet.class);
        CLASS_MAP.put(PathUtils.Holder.class.getName(),PathUtils.Holder.class);

        CONVERSION_MAP.put(String.class.getName(), identity());
        CONVERSION_MAP.put(Character.class.getName(), valueOf(Character.class));
        CONVERSION_MAP.put(Integer.class.getName(),valueOf(Integer.class));
        CONVERSION_MAP.put(Long.class.getName(), valueOf(Long.class));
        CONVERSION_MAP.put(Short.class.getName(), valueOf(Short.class));
        CONVERSION_MAP.put(Byte.class.getName(), valueOf(Byte.class));
        CONVERSION_MAP.put(Double.class.getName(), valueOf(Double.class));
        CONVERSION_MAP.put(Float.class.getName(), valueOf(Float.class));
        CONVERSION_MAP.put(Boolean.class.getName(), valueOf(Boolean.class));

        CONVERSION_MAP.put(char.class.getName(), valueOf(Character.class));
        CONVERSION_MAP.put(int.class.getName(), valueOf(Integer.class));
        CONVERSION_MAP.put(long.class.getName(), valueOf(Long.class));
        CONVERSION_MAP.put(short.class.getName(), valueOf(Short.class));
        CONVERSION_MAP.put(byte.class.getName(), valueOf(Byte.class));
        CONVERSION_MAP.put(double.class.getName(), valueOf(Double.class));
        CONVERSION_MAP.put(float.class.getName(), valueOf(Float.class));
        CONVERSION_MAP.put(boolean.class.getName(), valueOf(Boolean.class));

    }

    private static Object newInstance(String typeName) {
        try {
            return newInstance(Class.forName(typeName));
        } catch (ClassNotFoundException e) {
            throw new JavaPathRuntimeException("Failed new instance for type name "+typeName,e);
        }
    }


    private static Object newInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor(null);
            constructor.setAccessible(true);
            return constructor.newInstance(null);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    final Map<String, StringConverter<?>> conversionMap = new HashMap<>();
    final Map<String,Class<?>> classMap = new HashMap<>();
    final StringConverter<Function<?,?>> defaultSupplier = alias->{
        return type-> {
            if (type instanceof Class) {
                Class<?> aClass = (Class<?>) type;
                int modifiers = aClass.getModifiers();
                if( ! Modifier.isInterface(modifiers) && ! Modifier.isAbstract( aClass.getModifiers() )) {
                    return newInstance(aClass);
                }
            }
            if (classMap.containsKey(type.toString())) {
                return newInstance(classMap.get(type.toString()));
            }
            if (classMap.containsKey(alias)) {
                return newInstance(classMap.get(alias));
            }
            return newInstance(alias);
        };
    };

    public ClassRegistry() {
        this.classMap.putAll(CLASS_MAP);
        this.conversionMap.putAll(CONVERSION_MAP);
        conversionMap.put("key->new",defaultSupplier);
        conversionMap.put("new",typeName->defaultSupplier.apply(typeName));
    }

    public void registerClass(Class<?> aClass) {

    }

    public void registerClass(Class<?> aClass, String alias) {

    }

    public <T> void registerClass(Class<?> aClass, Function<String,T> converter) {

    }

    public <T> void registerClass(Class<?> aClass, String alias, StringConverter<T> converter) {
        Objects.requireNonNull(aClass,"Class<?> aClass required");
        if(alias != null && classMap.containsKey(alias) && ! aClass.equals(classMap.get(alias))) {
            throw new JavaPathRuntimeException("Class name alias "+alias+" for class "+aClass.getName()+" is already used for "+classMap.get(alias).getName());
        }
        if(alias != null && converter != null && conversionMap.containsKey(alias) && ! converter.equals(conversionMap.get(alias))) {
            throw new JavaPathRuntimeException("Conversion name alias "+alias+" for class "+aClass.getName()+" is already used for "+classMap.get(alias).getName());
        }
        String longName = aClass.getName();
        classMap.put(longName,aClass);
        classMap.put(longName,aClass);
        if(alias != null) {
            classMap.put(alias,aClass);
            if (converter != null) {
                conversionMap.put(longName, converter);
            }
        }
    }


    public void registerClassShortName(Class<?> aClass, String shortName) {
        Objects.requireNonNull(shortName,"registerClassShortName requires non empty name");
        Objects.requireNonNull(aClass,"registerClassShortName requires non empty class");
        classMap.put(shortName,aClass);
    }

    public static void registerGlobalClassShortName(Class<?> aClass, String shortName) {
        Objects.requireNonNull(shortName,"registerClassShortName requires non empty name");
        Objects.requireNonNull(aClass,"registerClassShortName requires non empty class");
        if(CLASS_MAP.containsKey(shortName) && ! aClass.equals(CLASS_MAP.get(shortName))) {
            throw new JavaPathRuntimeException("Short name "+shortName+" for class "+aClass.getSimpleName()+" already occupied by "+CLASS_MAP.get(shortName).getSimpleName());
        }
        CLASS_MAP.put(shortName,aClass);
    }

    public void registerClassSimpleName(Class<?> aClass) {
        registerClassShortName(aClass,aClass.getSimpleName());
    }

    public static void registerGlobalClassSimpleName(Class<?> aClass) {
        registerGlobalClassShortName(aClass,aClass.getSimpleName());
    }

    public <T> void registerStringConverter(Class<T> aClass, StringConverter<T> converter) {
        Objects.requireNonNull(aClass,"registerStringConverter requires non empty class");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+aClass.getSimpleName());
        conversionMap.put(aClass.getName(),converter);
    }

    public <T> void registerStringConverter(String alias, StringConverter<T> converter) {
        Objects.requireNonNull(alias,"registerStringConverter requires non empty class alias");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+alias);
        conversionMap.put(alias,converter);
    }

    public static <T> void registerGlobalStringConverter(Class<T> aClass, StringConverter<T> converter) {
        registerGlobalStringConverter(aClass.getName(),converter);
    }

    public static <T> void registerGlobalStringConverter(String alias, StringConverter<T> converter) {
        Objects.requireNonNull(alias,"registerStringConverter requires non empty class alias");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+alias);
        if(CONVERSION_MAP.containsKey(alias)) {
            throw new JavaPathRuntimeException("Alias name "+alias+" for converter is already in use");
        }
        CONVERSION_MAP.put(alias,converter);
    }

}
