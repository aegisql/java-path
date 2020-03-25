package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

public class ParametrizedProperty {

    private final ClassRegistry classRegistry;
    private final String propertyStr;
    private final Class<?> propertyType;
    private final boolean builder;
    private final boolean value;
    private final String typeAlias;

    private final Function<String,?> defaultConverter = cls -> {
        try {
            Constructor<?> constructor = getPropertyType().getConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(cls);
        } catch (Exception e) {
        }
        try {
            Method valueOf = getPropertyType().getMethod("valueOf", String.class);
            valueOf.setAccessible(true);
            return valueOf.invoke(null, cls);
        } catch (Exception e) {}
        throw new JavaPathRuntimeException("Failed to find instantiation method for " + cls);
    };

    public ParametrizedProperty(ClassRegistry classRegistry, TypedValue p) {
        this(classRegistry, p,false);
    }

    ParametrizedProperty(ClassRegistry classRegistry, TypedValue p, boolean forField) {
        this.classRegistry = classRegistry;
        Objects.requireNonNull(p,"Requires property");
            if(p.parametrized()) {
                this.typeAlias = p.getType();
                if(classRegistry.classMap.containsKey(p.getType())) {
                    propertyType = classRegistry.classMap.get(p.getType());
                    builder = false;
                    value = false;
                } else {
                    Class<?> aClass = toClass(p.getType());
                    if(aClass == null) {
                        propertyType = forField ? null : String.class;
                        builder = false;
                        value = false;
                    } else {
                        propertyType = classRegistry.classMap.computeIfAbsent(p.getType(), typeName->aClass);
                        builder = false;
                        value = false;
                    }
                }
            } else {
                this.typeAlias = null;
                if(p.isHashSign()) {
                    propertyType = null;
                    builder = true;
                    value = false;
                } else if(p.isDollarSign()) {
                    value = true;
                    builder = false;
                    if(classRegistry.classMap.containsKey(p.getType())) {
                        propertyType = classRegistry.classMap.get(p.getType());
                    } else if(p.getType() != null ){
                        propertyType = toClass(p.getType());
                    } else {
                        propertyType = null;
                    }
                } else {
                    propertyType = forField ? null : String.class;
                    builder = false;
                    value = false;
                }
            }
        this.propertyStr = p.getValue();
    }

    private Class<?> toClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getPropertyStr() {
        return propertyStr;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public boolean isBuilder() {
        return builder;
    }

    public boolean isValue() {
        return value;
    }

    public Object getProperty() {
        if(propertyType == null){
            return null;
        } else if(value) {
            return null;
        } else {
            Function<String, ?> supplier;
            if(classRegistry.conversionMap.containsKey(this.typeAlias)){
                supplier = classRegistry.conversionMap.get(this.typeAlias);
            } else {
                supplier = classRegistry.conversionMap.computeIfAbsent(propertyType.getName(), type -> defaultConverter);
            }
            return supplier.apply(propertyStr);
        }
    }

    String getTypeAlias() {
        return typeAlias;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LabelProperty{");
        sb.append("propertyStr='").append(propertyStr).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append(", builder=").append(builder);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
