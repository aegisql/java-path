package com.aegisql.java_path;

public class TypedValue {
    private String value;
    private String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if(type != null) {
            sb.append(type).append(" ");
        }
        sb.append(value);
        return sb.toString();
    }
}
