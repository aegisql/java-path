package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList {

    private final List<Object> references = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();
    private Object root;

    public ReferenceList(Object root) {
        this.root = root;
        this.references.add(root);
    }

    public ReferenceList(Object root, Object value) {
        this.root = root;
        this.references.add(root);
        this.values.add(value);
    }

    public ReferenceList addRoot(Object nextRoot) {
        if(nextRoot != root) {
            this.root = nextRoot;
            this.references.add(nextRoot);
        }
        return this;
    }

    public ReferenceList addValue(Object val) {
        values.add(val);
        return this;
    }

    public ReferenceList addReference(Object reference) {
        this.references.add(reference);
        return this;
    }

    public Object getRoot() {
        return root;
    }

    public Class<?> getRootClass() {
        return root.getClass();
    }

    public Object getReference(int i) {
        if(i < 0) {
            return root;
        } else {
            return references.get(i);
        }
    }

    public Class<?> getReferenceClass(int i) {
        return getReference(i).getClass();
    }

    public Object getValue(int i) {
        if(i < 0) {
            return values.get(0);
        } else {
            return values.get(i);
        }
    }

    public Class<?> getValueClass(int i) {
        Object val = getValue(i);
        if(val == null) {
            return null;
        } else {
            return val.getClass();
        }
    }

    public List<Object> getReferences() {
        return references;
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReferenceList{");
        sb.append("references=").append(references);
        sb.append(", root=").append(root);
        sb.append(", values=").append(values);
        sb.append('}');
        return sb.toString();
    }
}
