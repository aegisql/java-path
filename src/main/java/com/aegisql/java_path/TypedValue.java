package com.aegisql.java_path;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypedValue {
    private String value;
    private String type;
    private int backRefIdx = -1;
    private int valueIdx = -1;
    private LinkedList<TypedPathElement> typedPathElements = new LinkedList<>();

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
            if(i == 0 || i == chars.length-1){
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
        return value != null && value.startsWith("$");
    }

    public int getBackRefIdx() {
        return backRefIdx;
    }

    public void setBackRefIdx(int backRefIdx) {
        this.backRefIdx = backRefIdx;
    }

    public int getValueIdx() {
        return valueIdx;
    }

    public void setValueIdx(int valueIdx) {
        this.valueIdx = valueIdx;
    }

    public LinkedList<TypedPathElement> getTypedPathElements() {
        return typedPathElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedValue that = (TypedValue) o;
        return backRefIdx == that.backRefIdx &&
                valueIdx == that.valueIdx &&
                Objects.equals(value, that.value) &&
                Objects.equals(type, that.type) &&
                Objects.equals(typedPathElements, that.typedPathElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type, backRefIdx, valueIdx, typedPathElements);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if(type != null) {
            sb.append(type).append(" ");
        }
        if(backRefIdx < 0) {
            sb.append(value);
        } else {
            sb.append("#").append(backRefIdx);
        }
        if(typedPathElements.size() > 0) {
            sb.append(typedPathElements.stream().map(TypedPathElement::toString).collect(Collectors.joining(",", ".", "")));
        }
        return sb.toString();
    }
}
