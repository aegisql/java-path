package com.aegisql.java_path;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypedPathElement {

    private String name;
    private String type;
    private LinkedList<TypedValue> parameters = new LinkedList<>();
    private TypedValue typedValue = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedList<TypedValue> getParameters() {
        return parameters;
    }

    public void addParameter(TypedValue parameter) {
        this.parameters.add(parameter);
    }

    public boolean parametrized() {
        return this.parameters.size() > 0;
    }

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
        sb.append(name);
        if(parameters.size()>0) {
            sb.append(parameters.stream().map(p->""+p).collect(Collectors.joining(",","{","}")));
        }
        if(type != null) {
            sb.append(")");
        }
        return sb.toString();
    }

}
