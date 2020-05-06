package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Parametrized path.
 */
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

    /**
     * Instantiates a new Parametrized path.
     *
     * @param classRegistry the class registry
     * @param javaPath      the java path
     */
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

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get properties for getter object [ ].
     *
     * @param backRefObjects the back ref objects
     * @return the object [ ]
     */
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

    /**
     * Get properties for setter object [ ].
     *
     * @param backRefObjects the back ref objects
     * @return the object [ ]
     */
    public Object[] getPropertiesForSetter(ReferenceList backRefObjects) {
        boolean valueNotSet = true;
        List<Object> objects = new ArrayList<>();
        for(ParametrizedProperty lp:labelProperties) {
            if(lp.isPreEvaluatedValueSet()) {
                objects.add(lp.getPreEvaluatedValue());
                if(lp.isValue()) {
                    valueNotSet = false;
                }
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

    /**
     * Get classes for getter class [ ].
     *
     * @param backReferences the back references
     * @return the class [ ]
     */
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

    /**
     * Get classes for setter class [ ].
     *
     * @param backReferences the back references
     * @return the class [ ]
     */
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

    /**
     * Has value type boolean.
     *
     * @return the boolean
     */
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

    /**
     * Gets label properties.
     *
     * @return the label properties
     */
    List<ParametrizedProperty> getLabelProperties() {
        return labelProperties;
    }

    /**
     * Gets parametrized property.
     *
     * @return the parametrized property
     */
    ParametrizedProperty getParametrizedProperty() {
        return parametrizedProperty;
    }

    public TypedPathElement getPathElement() {
        return pathElement;
    }

}
