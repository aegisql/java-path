package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Reference list.
 */
public class ReferenceList {

    private final List<Object> references = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();
    private Object root;
    private final Object firstPathRoot;

    /**
     * Instantiates a new Reference list.
     *
     * @param root the root
     */
    public ReferenceList(Object root) {
        this.firstPathRoot = root;
        this.root = root;
        this.references.add(root);
    }

    /**
     * Instantiates a new Reference list.
     *
     * @param root  the root
     * @param value the value
     */
    public ReferenceList(Object root, Object value) {
        this.firstPathRoot = root;
        this.root = root;
        this.references.add(root);
        this.values.add(value);
    }

    /**
     * Add root reference list.
     *
     * @param nextRoot the next root
     * @return the reference list
     */
    public ReferenceList addRoot(Object nextRoot) {
        if(nextRoot != root) {
            this.root = nextRoot;
            this.references.add(nextRoot);
        }
        return this;
    }

    /**
     * Add value reference list.
     *
     * @param val the val
     * @return the reference list
     */
    public ReferenceList addValue(Object val) {
        values.add(val);
        return this;
    }

    /**
     * Add reference reference list.
     *
     * @param reference the reference
     * @return the reference list
     */
    public ReferenceList addReference(Object reference) {
        this.references.add(reference);
        return this;
    }

    /**
     * Gets root.
     *
     * @return the root
     */
    public Object getRoot() {
        return root;
    }

    /**
     * Gets root class.
     *
     * @return the root class
     */
    public Class<?> getRootClass() {
        return root.getClass();
    }

    public Class<?> getFirstPathRootClass() {
        return firstPathRoot.getClass();
    }

    /**
     * Gets reference.
     *
     * @param i the
     * @return the reference
     */
    public Object getReference(int i) {
        if(i < 0) {
            return root;
        } else {
            return references.get(i);
        }
    }

    /**
     * Gets reference class.
     *
     * @param i the
     * @return the reference class
     */
    public Class<?> getReferenceClass(int i) {
        return getReference(i).getClass();
    }

    /**
     * Gets value.
     *
     * @param i the
     * @return the value
     */
    public Object getValue(int i) {
        if(i < 0) {
            return values.get(0);
        } else {
            return values.get(i);
        }
    }

    /**
     * Gets value class.
     *
     * @param i the
     * @return the value class
     */
    public Class<?> getValueClass(int i) {
        Object val = getValue(i);
        if(val == null) {
            return null;
        } else {
            return val.getClass();
        }
    }

    /**
     * Gets references.
     *
     * @return the references
     */
    public List<Object> getReferences() {
        return references;
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

    public ReferenceList startNextPath(){
        ReferenceList nextList = new ReferenceList(firstPathRoot);
        nextList.values.addAll(this.values);
        return nextList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReferenceList{");
        sb.append("references=").append(references);
        if(firstPathRoot != root) {
            sb.append(", firstPathRoot=").append(firstPathRoot);
        }
        sb.append(", root=").append(root);
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }
}
