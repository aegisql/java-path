package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParametrizedPath {

    private final static Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[]{};
    private final static Object[] EMPTY_OBJECT_ARRAY = new Object[]{};

    private final ClassRegistry classRegistry;
    private final TypedPathElement pathElement;
    private final String wholeLabel;
    private final String label;
    private boolean hasValueType = false;
    private final List<ParametrizedProperty> labelProperties;
    private final ParametrizedProperty parametrizedProperty;

 /*    public ParametrizedPath(Class<?> bClass, String label) {
        this(new ClassRegistry(), bClass,null,label);
    }

   public ParametrizedPath(ClassRegistry classRegistry, Class<?> bClass, Class<?> vClass, String label) {
        Objects.requireNonNull(label,"Label must not be null");
        this.classRegistry = classRegistry == null? new ClassRegistry():classRegistry;
        this.pathElement = new TypedPathElement();
        this.pathElement.setName(label);
        this.wholeLabel = label.trim();
        if(isParametrized()) {
            String[] parts = label.split("\\{|\\}",3);
            this.parametrizedProperty = new ParametrizedProperty(parts[0],true);
            this.label = parametrizedProperty.getPropertyStr();
            String[] properties = parts[1].split(",");
            this.labelProperties = Arrays.stream(properties)
                    .map(ParametrizedProperty::new)
                    .peek(lp->{if(lp.isValue() && lp.getPropertyType() != null) hasValueType = true;})
                    .collect(Collectors.toList());
        } else {
            this.labelProperties = Collections.EMPTY_LIST;
            this.parametrizedProperty = new ParametrizedProperty(label,true);
            this.label = parametrizedProperty.getPropertyStr();
        }
    }*/

    public ParametrizedPath(ClassRegistry classRegistry, Class<?> aClass, TypedPathElement javaPath) {
        this.classRegistry = classRegistry == null? new ClassRegistry():classRegistry;
        this.pathElement = javaPath;
        this.wholeLabel = javaPath.toString();
        this.label = javaPath.getName();
        this.parametrizedProperty = new ParametrizedProperty(classRegistry,pathElement.getOwnTypedValue(),true);

        if(this.pathElement.parametrized()) {
            this.labelProperties = pathElement.getParameters()
                    .stream()
                    .map(tv-> new ParametrizedProperty(classRegistry,tv,false))
                    .peek(lp->{if(lp.isValue() && lp.getPropertyType() != null) hasValueType = true;})
                    .collect(Collectors.toList());
        } else {
            this.labelProperties = Collections.EMPTY_LIST;
        }
    }

    private boolean isParametrized() {
        return wholeLabel.endsWith("}") && wholeLabel.contains("{");
    }

    public String getLabel() {
        return label;
    }

    public Object[] getPropertiesForGetter(Object builder, Object value) {
        List<Object> objects = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                objects.add(builder);
            } else if(lp.isValue()) {
                objects.add( value );
            } else {
                objects.add(lp.getProperty());
            }
        }
        return objects.toArray(EMPTY_OBJECT_ARRAY);
    }

    public Object[] getPropertiesForSetter(Object builder, Object value) {
        boolean valueNotSet = true;
        List<Object> objects = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                objects.add(builder);
            } else if(lp.isValue()) {
                objects.add( value );
                valueNotSet = false;
            } else {
                objects.add(lp.getProperty());
            }
        }
        if(valueNotSet) {
            objects.add( value );
        }
        return objects.toArray(EMPTY_OBJECT_ARRAY);
    }

    public Class<?>[] getClassesForGetter(Class<?> bClass, Class<?> vClass) {
        List<Class<?>> classes = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                classes.add(bClass);
            } else if(lp.isValue()) {
                classes.add( lp.getPropertyType() == null ? vClass : lp.getPropertyType() );
            } else {
                classes.add(lp.getPropertyType());
            }
        }
        return classes.toArray(EMPTY_CLASS_ARRAY);
    }

    public Class<?>[] getClassesForSetter(Class<?> bClass, Class<?> vClass) {
        boolean valueNotSet = true;
        List<Class<?>> classes = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                classes.add(bClass);
            } else if(lp.isValue()) {
                classes.add( lp.getPropertyType() == null ? vClass : lp.getPropertyType() );
                valueNotSet = false;
            } else {
                classes.add(lp.getPropertyType());
            }
        }
        if(valueNotSet) {
            classes.add(vClass);
        }
        return classes.toArray(EMPTY_CLASS_ARRAY);
    }

    public boolean hasValueType() {
        return hasValueType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParametrizedLabel{");
        sb.append(wholeLabel);
        sb.append('}');
        return sb.toString();
    }

    List<ParametrizedProperty> getLabelProperties() {
        return labelProperties;
    }

    ParametrizedProperty getParametrizedProperty() {
        return parametrizedProperty;
    }
}
