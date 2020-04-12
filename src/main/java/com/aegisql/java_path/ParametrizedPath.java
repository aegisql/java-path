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

    public ParametrizedPath(ClassRegistry classRegistry, TypedPathElement javaPath) {
        this.classRegistry = classRegistry == null? new ClassRegistry():classRegistry;
        this.pathElement = javaPath;
        this.wholeLabel = javaPath.toString();
        this.label = javaPath.getName();
        this.parametrizedProperty = new ParametrizedProperty(this.classRegistry,pathElement.getOwnTypedValue(),true);

        if(this.pathElement.parametrized()) {
            this.labelProperties = pathElement.getParameters()
                    .stream()
                    .map(tv-> new ParametrizedProperty(this.classRegistry,tv,false))
                    .peek(lp->{if(lp.isValue() && lp.getPropertyType() != null) hasValueType = true;})
                    .collect(Collectors.toList());
        } else {
            this.labelProperties = Collections.EMPTY_LIST;
        }
    }

    public String getLabel() {
        return label;
    }

    public Object[] getPropertiesForGetter(ReferenceList backRefObjects) {
        List<Object> objects = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isPreEvaluatedValueSet()) {
                objects.add(lp.getPreEvaluatedValue());
            } else if(lp.isBuilder()) {
                objects.add(backRefObjects.getReference(lp.getBackReferenceIdx()));
            } else if(lp.isValue()) {
                objects.add( backRefObjects.getValue(lp.getValueIdx()) );
            } else if(lp.isBackReferenceIdx()) {
                objects.add(backRefObjects.getReference(lp.getBackReferenceIdx()));
            } else {
                objects.add(lp.getProperty());
            }
        }
        return objects.toArray(EMPTY_OBJECT_ARRAY);
    }

    public Object[] getPropertiesForSetter(ReferenceList backRefObjects) {
        boolean valueNotSet = true;
        List<Object> objects = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isPreEvaluatedValueSet()) {
                objects.add(lp.getPreEvaluatedValue());
            } else if(lp.isBuilder()) {
                objects.add(backRefObjects.getRoot());
            } else if(lp.isValue()) {
                objects.add( backRefObjects.getValue(lp.getValueIdx()) );
                valueNotSet = false;
            } else if(lp.isBackReferenceIdx()) {
                objects.add( backRefObjects.getReference(lp.getBackReferenceIdx()) );
            } else {
                objects.add(lp.getProperty());
            }
        }
        if(valueNotSet) {
            objects.add( backRefObjects.getValue(0) );
        }
        return objects.toArray(EMPTY_OBJECT_ARRAY);
    }

    public Class<?>[] getClassesForGetter(ReferenceList backReferences) {
        List<Class<?>> classes = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                classes.add(backReferences.getReferenceClass(lp.getBackReferenceIdx()));
            } else if(lp.isValue()) {
                classes.add( lp.getPropertyType() == null ? backReferences.getValueClass(lp.getValueIdx()) : lp.getPropertyType() );
            } else if(lp.isBackReferenceIdx()) {
                classes.add(backReferences.getReferenceClass(lp.getBackReferenceIdx()));
            } else {
                classes.add(lp.getPropertyType());
            }
        }
        return classes.toArray(EMPTY_CLASS_ARRAY);
    }

    public Class<?>[] getClassesForSetter(ReferenceList backReferences) {
        boolean valueNotSet = true;
        List<Class<?>> classes = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isBuilder()) {
                classes.add(backReferences.getRootClass());
            } else if(lp.isValue()) {
                classes.add( lp.getPropertyType() == null ? backReferences.getValueClass(lp.getValueIdx()) : lp.getPropertyType() );
                valueNotSet = false;
            } else if(lp.isBackReferenceIdx()) {
                classes.add(backReferences.getReferenceClass(lp.getBackReferenceIdx()));
            } else {
                classes.add(lp.getPropertyType());
            }
        }
        if(valueNotSet) {
            classes.add(backReferences.getValueClass(0));
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
