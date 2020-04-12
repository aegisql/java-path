package com.aegisql.java_path;

import java.util.Objects;

public class ParametrizedProperty {

    private final ClassRegistry classRegistry;
    private final String propertyStr;
    private final Class<?> propertyType;
    private final boolean builder;
    private final boolean value;
    private final int backReferenceIdx;
    private final int valueIdx;
    private final String typeAlias;
    private final Object preEvaluatedValue;
    private final boolean preEvaluatedValueSet;

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

    public ParametrizedProperty(ClassRegistry classRegistry, TypedValue typedValue) {
        this(classRegistry, typedValue,false);
    }

    ParametrizedProperty(ClassRegistry classRegistry, TypedValue typedValue, boolean forField) {
        this.classRegistry = classRegistry;
        this.value = typedValue.isDollarSign();
        this.backReferenceIdx = typedValue.getBackRefIdx();
        this.valueIdx = typedValue.getValueIdx();
        this.preEvaluatedValue = typedValue.getPreEvaluatedValue();
        this.preEvaluatedValueSet = typedValue.isPreEvaluatedValueSet();
        Objects.requireNonNull(typedValue,"Requires property");
            if(typedValue.parametrized()) {
                this.typeAlias = typedValue.getType();
                if(classRegistry.classMap.containsKey(typedValue.getType())) {
                    propertyType = classRegistry.classMap.get(typedValue.getType());
                    builder = false;
                } else {
                    Class<?> aClass = toClass(typedValue.getType());
                    if(aClass == null) {
                        propertyType = forField ? null : String.class;
                        builder = false;
                    } else {
                        propertyType = classRegistry.classMap.computeIfAbsent(typedValue.getType(), typeName->aClass);
                        builder = false;
                    }
                }
            } else {
                this.typeAlias = null;
                if(typedValue.isHashSign()) {
                    propertyType = null;
                    builder = true;
                } else if(typedValue.isDollarSign()) {
                    builder = false;
                    if(classRegistry.classMap.containsKey(typedValue.getType())) {
                        propertyType = classRegistry.classMap.get(typedValue.getType());
                    } else if(typedValue.getType() != null ){
                        propertyType = toClass(typedValue.getType());
                    } else {
                        propertyType = null;
                    }
                } else {
                    propertyType = forField ? null : String.class;
                    builder = false;
                }
            }
        this.propertyStr = typedValue.getValue();
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

    public boolean isBackReferenceIdx() {
        return backReferenceIdx >= 0;
    }

    public boolean isValueIdx() {
        return valueIdx >= 0;
    }

    public int getBackReferenceIdx() {
        return backReferenceIdx;
    }

    public int getValueIdx() {
        return valueIdx;
    }

    public Object getPreEvaluatedValue(){
        return preEvaluatedValue;
    }

    public boolean isPreEvaluatedValueSet() {
        return preEvaluatedValueSet;
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
        sb.append(", typeAlias='").append(typeAlias).append('\'');
        sb.append(", builder=").append(builder);
        sb.append(", value=").append(value);
        sb.append(", backReferenceIdx=").append(backReferenceIdx);
        sb.append(", valueIdx=").append(valueIdx);
        sb.append('}');
        return sb.toString();
    }
}
