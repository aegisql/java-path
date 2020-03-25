package com.aegisql.java_path;

import java.util.Objects;

public class TypedValue {
    private String value;
    private String type;

    public String getValue() {
        return value;
    }

    private String unEscape(String s) {
        if(s == null) return null;
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean escFound = false;
        for(int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if(i == 0 || i == chars.length -1){
                if(ch == '\'') continue;
                if(ch == '"') continue;
            }
            if(ch == '\\' && ! escFound) {
                escFound = true;
                continue;
            }
            sb.append(ch);
            escFound = false;
        }
        return sb.toString();
    }

    public void setValue(String value) {
        this.value = unEscape(value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean parametrized() {
        return type != null && ! "".equals(type);
    }

    public boolean isHashSign() {
        return "#".equals(value);
    }

    public boolean isDollarSign() {
        return "$".equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedValue that = (TypedValue) o;
        return value.equals(that.value) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
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
