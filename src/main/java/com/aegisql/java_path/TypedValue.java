package com.aegisql.java_path;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Typed value.
 */
public class TypedValue {
    private String value;
    private String type;
    private int backRefIdx = -1;
    private int valueIdx = -1;
    private LinkedList<TypedPathElement> typedPathElements = new LinkedList<>();
    private Object preEvaluatedValue = null;
    private boolean preEvaluatedValueSet = false;

    /**
     * Gets value.
     *
     * @return the value
     */
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

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = unEscape(value);
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
     * Parametrized boolean.
     *
     * @return the boolean
     */
    public boolean parametrized() {
        return type != null && ! "".equals(type);
    }

    /**
     * Is hash sign boolean.
     *
     * @return the boolean
     */
    public boolean isHashSign() {
        return value != null && value.startsWith("#");
    }

    /**
     * Is dollar sign boolean.
     *
     * @return the boolean
     */
    public boolean isDollarSign() {
        return value != null && value.startsWith("$");
    }

    /**
     * Gets back ref idx.
     *
     * @return the back ref idx
     */
    public int getBackRefIdx() {
        return backRefIdx;
    }

    /**
     * Sets back ref idx.
     *
     * @param backRefIdx the back ref idx
     */
    public void setBackRefIdx(int backRefIdx) {
        this.backRefIdx = backRefIdx;
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
     * Sets value idx.
     *
     * @param valueIdx the value idx
     */
    public void setValueIdx(int valueIdx) {
        this.valueIdx = valueIdx;
    }

    /**
     * Sets typed path elements.
     *
     * @param typedPathElements the typed path elements
     */
    public void setTypedPathElements(LinkedList<TypedPathElement> typedPathElements) {
        this.typedPathElements = typedPathElements;
    }

    /**
     * Gets typed path elements.
     *
     * @return the typed path elements
     */
    public LinkedList<TypedPathElement> getTypedPathElements() {
        return typedPathElements;
    }

    /**
     * Has path boolean.
     *
     * @return the boolean
     */
    public boolean hasPath() {
        return typedPathElements.size() > 0;
    }

    /**
     * Gets pre evaluated value.
     *
     * @return the pre evaluated value
     */
    public Object getPreEvaluatedValue() {
        return preEvaluatedValue;
    }

    /**
     * Sets pre evaluated value.
     *
     * @param preEvaluatedValue the pre evaluated value
     */
    public void setPreEvaluatedValue(Object preEvaluatedValue) {
        this.preEvaluatedValue = preEvaluatedValue;
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
     * Sets pre evaluated value set.
     *
     * @param preEvaluatedValueSet the pre evaluated value set
     */
    public void setPreEvaluatedValueSet(boolean preEvaluatedValueSet) {
        this.preEvaluatedValueSet = preEvaluatedValueSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedValue that = (TypedValue) o;
        return backRefIdx == that.backRefIdx &&
                valueIdx == that.valueIdx &&
                preEvaluatedValueSet == that.preEvaluatedValueSet &&
                Objects.equals(value, that.value) &&
                Objects.equals(type, that.type) &&
                Objects.equals(typedPathElements, that.typedPathElements) &&
                Objects.equals(preEvaluatedValue, that.preEvaluatedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type, backRefIdx, valueIdx, typedPathElements, preEvaluatedValue, preEvaluatedValueSet);
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
