package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.List;

public class ReferenceList {

    private final List<Object> references = new ArrayList<>();
    private Object root;

    public ReferenceList(Object initial) {
        this.root = initial;
        this.references.add(initial);
    }

    public ReferenceList addRoot(Object nextRoot) {
        this.root = nextRoot;
        this.references.add(nextRoot);
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReferenceList{");
        sb.append("references=").append(references);
        sb.append(", root=").append(root);
        sb.append('}');
        return sb.toString();
    }
}
