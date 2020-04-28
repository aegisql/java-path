package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static com.aegisql.java_path.StringConverter.identity;
import static com.aegisql.java_path.StringConverter.valueOf;

/**
 * The type Class registry.
 */
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
        registerGlobalClass(void.class,"void");
        registerGlobalClass(String.class,String.class.getSimpleName(),"s","str","string");
        registerGlobalClass(int.class,int.class.getSimpleName(),"i");
        registerGlobalClass(Integer.class,Integer.class.getSimpleName(),"I","Int");
        registerGlobalClass(long.class,long.class.getSimpleName(),"l");
        registerGlobalClass(Long.class,Long.class.getSimpleName(),"L");
        registerGlobalClass(byte.class,byte.class.getSimpleName(),"b");
        registerGlobalClass(Byte.class,Byte.class.getSimpleName(),"B");
        registerGlobalClass(boolean.class,boolean.class.getSimpleName(),"bool");
        registerGlobalClass(Boolean.class,Boolean.class.getSimpleName(),"Bool");
        registerGlobalClass(char.class,char.class.getSimpleName(),"c","ch");
        registerGlobalClass(Character.class,Character.class.getSimpleName(),"C","Ch","Char");
        registerGlobalClass(short.class,short.class.getSimpleName());
        registerGlobalClass(Short.class,Short.class.getSimpleName());
        registerGlobalClass(double.class,double.class.getSimpleName(),"d");
        registerGlobalClass(Double.class,Double.class.getSimpleName(),"D");
        registerGlobalClass(float.class,float.class.getSimpleName(),"f");
        registerGlobalClass(Float.class,Float.class.getSimpleName(),"F");

        registerGlobalClass(Function.class,"new","key->new");
        registerGlobalClass(ArrayList.class,ArrayList.class.getSimpleName(),"list");
        registerGlobalClass(LinkedList.class,LinkedList.class.getSimpleName(),"linkedlist");
        registerGlobalClass(HashMap.class,HashMap.class.getSimpleName(),"map");
        registerGlobalClass(HashSet.class,HashSet.class.getSimpleName(),"set");
        registerGlobalClass(TreeMap.class,TreeMap.class.getSimpleName(),"treemap");
        registerGlobalClass(TreeSet.class,TreeSet.class.getSimpleName(),"treeset");
        registerGlobalClass(JavaPath.Holder.class);

        registerGlobalStringConverter(identity(),String.class.getName(),String.class.getSimpleName(),"string","str","s");
        registerGlobalStringConverter(valueOf(Integer.class),Integer.class.getName(),Integer.class.getSimpleName(),"Int","I","integer","int","i");
        registerGlobalStringConverter(valueOf(Long.class),Long.class.getName(),Long.class.getSimpleName(),"L","long","l");
        registerGlobalStringConverter(valueOf(Short.class),Short.class.getName(),Short.class.getSimpleName(),"short");
        registerGlobalStringConverter(valueOf(Byte.class),Byte.class.getName(),Byte.class.getSimpleName(),"byte","B","b");
        registerGlobalStringConverter(valueOf(Double.class),Double.class.getName(),Double.class.getSimpleName(),"D","d","double");
        registerGlobalStringConverter(valueOf(Float.class),Float.class.getName(),Float.class.getSimpleName(),"F","f","float");
        registerGlobalStringConverter(valueOf(Boolean.class),Boolean.class.getName(),Boolean.class.getSimpleName(),"Bool","bool","boolean");
        registerGlobalStringConverter(str->{
            Objects.requireNonNull(str);
            if(str.length() > 1) {
                throw new JavaPathRuntimeException("Cannot convert "+str+" to a single char");
            }
            return str.charAt(0);
        },Character.class.getName(),Character.class.getSimpleName(),"Char","Ch","C","char","ch","c");
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

    /**
     * The Conversion map.
     */
    final Map<String, StringConverter<?>> conversionMap = new HashMap<>();
    /**
     * The Class map.
     */
    final Map<String,Class<?>> classMap = new HashMap<>();
    /**
     * The Default supplier.
     */
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

    /**
     * Instantiates a new Class registry.
     */
    public ClassRegistry() {
        this.classMap.putAll(CLASS_MAP);
        this.conversionMap.putAll(CONVERSION_MAP);
        conversionMap.put("key->new",defaultSupplier);
        conversionMap.put("new",typeName->defaultSupplier.apply(typeName));
    }

    /**
     * Gets converter.
     *
     * @param names the names
     * @return the converter
     */
    public Optional<StringConverter> getConverter(String... names) {
        if(names == null || names.length == 0) {
            return Optional.empty();
        } else {
            for(String name: names){
                if(conversionMap.containsKey(name)) {
                    return Optional.of(conversionMap.get(name));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Register class.
     *
     * @param aClass the a class
     * @param names  the names
     */
    public void registerClass(Class<?> aClass, String... names) {
        Objects.requireNonNull(aClass,"Cannot register NULL as a class");
        classMap.put(aClass.getName(),aClass);
        if(names != null && names.length > 0) {
            Arrays.stream(names).filter(Objects::nonNull).forEach(name->classMap.computeIfAbsent(name,nm->aClass));
        }
    }

    /**
     * Register global class.
     *
     * @param aClass the a class
     * @param names  the names
     */
    public static void registerGlobalClass(Class<?> aClass, String... names) {
        CLASS_MAP.put(aClass.getName(),aClass);
        if(names != null || names.length > 0) {
            Arrays.stream(names).filter(Objects::nonNull).forEach(name->CLASS_MAP.computeIfAbsent(name,nm->aClass));
        }
    }

    /**
     * Register string converter string converter.
     *
     * @param <T>       the type parameter
     * @param aClass    the a class
     * @param converter the converter
     * @return the string converter
     */
    public <T> StringConverter<T> registerStringConverter(Class<?> aClass, StringConverter<T> converter) {
        Objects.requireNonNull(aClass,"registerStringConverter requires non empty class");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+aClass.getSimpleName());
        return registerStringConverter(converter,aClass.getName());
    }

    /**
     * Register string converter string converter.
     *
     * @param <T>       the type parameter
     * @param converter the converter
     * @param names     the names
     * @return the string converter
     */
    public <T> StringConverter<T> registerStringConverter(StringConverter<T> converter,String... names) {
        Objects.requireNonNull(names,"registerStringConverter requires non empty class alias names");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for classes "+String.join(",",names));
        Arrays.stream(names).filter(Objects::nonNull).forEach(name->conversionMap.computeIfAbsent(name,clsName->converter));
        return converter;
    }

    /**
     * Register global string converter.
     *
     * @param <T>       the type parameter
     * @param aClass    the a class
     * @param converter the converter
     */
    public static <T> void registerGlobalStringConverter(Class<T> aClass, StringConverter<T> converter) {
        registerGlobalStringConverter(converter,aClass.getName());
    }

    /**
     * Register global string converter.
     *
     * @param <T>       the type parameter
     * @param converter the converter
     * @param names     the names
     */
    public static <T> void registerGlobalStringConverter(StringConverter<T> converter, String... names) {
        Objects.requireNonNull(names,"registerStringConverter requires non empty class alias");
        Objects.requireNonNull(converter,"registerStringConverter requires converter for class "+String.join(",",names));
        Arrays.stream(names).filter(Objects::nonNull).forEach(alias->CONVERSION_MAP.computeIfAbsent(alias,name->converter));
    }

}
