package com.aegisql.java_path;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class TypedPathElement {

    private String name;
    private String type;
    private LinkedList<TypedValue> parameters = new LinkedList<>();

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if(type != null){
            sb.append("(").append(type).append(" ");
        }
        sb.append(name);
        if(parameters.size()>0) {
            sb.append(parameters.stream().map(p->p.toString()).collect(Collectors.joining(",","{","}")));
        }
        if(type != null) {
            sb.append(")");
        }
        return sb.toString();
    }
}
