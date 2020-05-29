package com.aegisql.java_path;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * The type Parametrized property.
 */
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
    private final Method factory;

    private static StringConverter<?> defaultConverter(Class<?> cls) {
        StringConverter<?> constructor = StringConverter.constructor(cls);
        if(constructor != null) {
            return strVal->constructor.apply(strVal);
        }
        StringConverter<?> converter = StringConverter.valueOf(cls);
        if(converter != null) {
            return strVal->converter.apply(strVal);
        }
        return strVal->{throw new JavaPathRuntimeException("Failed to find conversion method for string value '" + strVal + "' type " + cls);};
    }

    /**
     * Instantiates a new Parametrized property.
     *
     * @param classRegistry the class registry
     * @param typedValue    the typed value
     * @param forField      the for field
     */
    ParametrizedProperty(ClassRegistry classRegistry, TypedValue typedValue, boolean forField) {
        this.classRegistry = classRegistry;
        this.value = typedValue.isDollarSign();
        this.backReferenceIdx = typedValue.getBackRefIdx();
        this.valueIdx = typedValue.getValueIdx();
        this.preEvaluatedValue = typedValue.getPreEvaluatedValue();
        this.preEvaluatedValueSet = typedValue.isPreEvaluatedValueSet();
        this.propertyStr = typedValue.getValue();

        Class<?> type = null;
        Objects.requireNonNull(typedValue,"Requires property");
            if(typedValue.parametrized()) {
                this.typeAlias = typedValue.getType();
                if(classRegistry.classMap.containsKey(typedValue.getType())) {
                    type = classRegistry.classMap.get(typedValue.getType());
                    builder = false;
                } else {
                    Class<?> aClass = toClass(typedValue.getType());
                    if(aClass == null) {
                        type = forField ? null : String.class;
                        builder = false;
                    } else {
                        type = classRegistry.classMap.computeIfAbsent(typedValue.getType(), typeName->aClass);
                        builder = false;
                    }
                }
                if(typedValue.getFactory() != null) {
                    try {
                        this.factory = type.getMethod(typedValue.getFactory(),String.class);
                        type = this.factory.getReturnType();
                    } catch (NoSuchMethodException e) {
                        throw new JavaPathRuntimeException("failed to find factory "+typedValue.getFactory()+" for "+propertyStr,e);
                    }
                } else {
                    this.factory = null;
                }
            } else {
                this.typeAlias = null;
                this.factory = null;
                if(typedValue.isHashSign()) {
                    type = null;
                    builder = true;
                } else if(typedValue.isDollarSign()) {
                    builder = false;
                    if(classRegistry.classMap.containsKey(typedValue.getType())) {
                        type = classRegistry.classMap.get(typedValue.getType());
                    } else if(typedValue.getType() != null ){
                        type = toClass(typedValue.getType());
                    } else {
                        type = null;
                    }
                } else {
                    type = forField ? null : String.class;
                    builder = false;
                }
            }
            this.propertyType = type;
    }

    private Class<?> toClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets property str.
     *
     * @return the property str
     */
    public String getPropertyStr() {
        return propertyStr;
    }

    /**
     * Gets property type.
     *
     * @return the property type
     */
    public Class<?> getPropertyType() {
        return propertyType;
    }

    /**
     * Is builder boolean.
     *
     * @return the boolean
     */
    public boolean isBuilder() {
        return builder;
    }

    /**
     * Is value boolean.
     *
     * @return the boolean
     */
    public boolean isValue() {
        return value;
    }

    /**
     * Is back reference idx boolean.
     *
     * @return the boolean
     */
    public boolean isBackReferenceIdx() {
        return backReferenceIdx >= 0;
    }

    /**
     * Is value idx boolean.
     *
     * @return the boolean
     */
    public boolean isValueIdx() {
        return valueIdx >= 0;
    }

    /**
     * Gets back reference idx.
     *
     * @return the back reference idx
     */
    public int getBackReferenceIdx() {
        return backReferenceIdx;
    }

    /**
     * Gets value idx.
     *
     * @return the value idx
     */
    public int getValueIdx() {
        return valueIdx;
    }

    /**
     * Get pre evaluated value object.
     *
     * @return the object
     */
    public Object getPreEvaluatedValue(){
        return preEvaluatedValue;
    }

    /**
     * Is pre evaluated value set boolean.
     *
     * @return the boolean
     */
    public boolean isPreEvaluatedValueSet() {
        return preEvaluatedValueSet;
    }

    /**
     * Gets property.
     *
     * @return the property
     */
    public Object getProperty() {
        if(propertyType == null){
            return null;
        } else if(value) {
            return null;
        } else if(factory != null) {
            try {
                return factory.invoke(null,propertyStr);
            } catch (Exception e) {
                throw new JavaPathRuntimeException("Exception in "+this,e);
            }
        } else {
            StringConverter<?> supplier = classRegistry
                    .getConverter(this.typeAlias,this.propertyType.getName())
                    .orElseGet(()->classRegistry.registerStringConverter(defaultConverter(propertyType),typeAlias,propertyType.getName()));
            return supplier.apply(propertyStr);
        }
    }

    /**
     * Gets type alias.
     *
     * @return the type alias
     */
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
        if(factory != null) {
            sb.append(", factory=").append(factory.getName());
        }
        sb.append(", backReferenceIdx=").append(backReferenceIdx);
        sb.append(", valueIdx=").append(valueIdx);
        sb.append('}');
        return sb.toString();
    }
}
