package com.aegisql.java_path;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Typed path element.
 */
public class TypedPathElement {

    private String name;
    private String type;
    private LinkedList<TypedValue> parameters = new LinkedList<>();
    private TypedValue typedValue = null;
    private TypedPathElement optionalPathElement;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets parameters.
     *
     * @return the parameters
     */
    public LinkedList<TypedValue> getParameters() {
        return parameters;
    }

    /**
     * Add parameter.
     *
     * @param parameter the parameter
     */
    public void addParameter(TypedValue parameter) {
        this.parameters.add(parameter);
    }

    /**
     * Parametrized boolean.
     *
     * @return the boolean
     */
    public boolean parametrized() {
        return this.parameters.size() > 0;
    }

    /**
     * Gets own typed value.
     *
     * @return the own typed value
     */
    public TypedValue getOwnTypedValue() {
        if(typedValue == null) {
            typedValue = new TypedValue();
            typedValue.setType(type);
            typedValue.setValue(name);
        }
        return typedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedPathElement that = (TypedPathElement) o;
        return name.equals(that.name) &&
                Objects.equals(type, that.type) &&
                parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, parameters);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if(type != null){
            sb.append("(").append(type).append(" ");
        }
        sb.append(name == null ? "?" : name);
        if(parameters.size()>0) {
            sb.append(parameters.stream().map(p->""+p).collect(Collectors.joining(",","(",")")));
        }
        if(type != null) {
            sb.append(")");
        }
        if(optionalPathElement!= null) {
            sb.append("?").append(optionalPathElement.toString());
        }
        return sb.toString();
    }

    public void setOptionalPathElement(TypedPathElement optionalPathElement) {
        this.optionalPathElement = optionalPathElement;
    }

    public TypedPathElement getOptionalPathElement() {
        return optionalPathElement;
    }
}
