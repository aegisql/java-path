package com.aegisql.java_path;

import java.util.Objects;

public class ParametrizedProperty {

    private final ClassRegistry classRegistry;
    private final String propertyStr;
    private final Class<?> propertyType;
    private final boolean builder;
    private final boolean value;
    private final int backReference;
    private final String typeAlias;

    private final StringConverter<?> defaultConverter = cls -> {
        StringConverter<?> converter = StringConverter.constructor(getPropertyType());
        if(converter != null) {
            return converter.apply(cls);
        }
        converter = StringConverter.valueOf(getPropertyType());
        if(converter != null) {
            return converter.apply(cls);
        }
        throw new JavaPathRuntimeException("Failed to find instantiation method for " + cls);
    };

    public ParametrizedProperty(ClassRegistry classRegistry, TypedValue p) {
        this(classRegistry, p,false);
    }

    ParametrizedProperty(ClassRegistry classRegistry, TypedValue p, boolean forField) {
        this.classRegistry = classRegistry;
        this.value = p.isDollarSign();
        this.backReference = p.getBackRef();
        Objects.requireNonNull(p,"Requires property");
            if(p.parametrized()) {
                this.typeAlias = p.getType();
                if(classRegistry.classMap.containsKey(p.getType())) {
                    propertyType = classRegistry.classMap.get(p.getType());
                    builder = false;
                } else {
                    Class<?> aClass = toClass(p.getType());
                    if(aClass == null) {
                        propertyType = forField ? null : String.class;
                        builder = false;
                    } else {
                        propertyType = classRegistry.classMap.computeIfAbsent(p.getType(), typeName->aClass);
                        builder = false;
                    }
                }
            } else {
                this.typeAlias = null;
                if(p.isHashSign()) {
                    propertyType = null;
                    builder = true;
                } else if(p.isDollarSign()) {
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

    public boolean isBackReference() {
        return backReference >= 0;
    }

    public int getBackReference() {
        return backReference;
    }

    public Object getProperty() {
        if(propertyType == null){
            return null;
        } else if(value) {
            return null;
        } else {
            StringConverter<?> supplier;
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
        final StringBuilder sb = new StringBuilder("ParametrizedProperty{");
        sb.append("propertyStr='").append(propertyStr).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append(", builder=").append(builder);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
