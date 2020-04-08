package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo13 {

    public static class A {
        A mother;
        A father;
        String name;

        public A() {}

        public A(String name) {
            this.name = name;
        }

        public A mother(A mother) {
            this.mother = mother;
            return this;
        }

        public A father(A father) {
            this.father = father;
            return this;
        }

    }

    @Test
    public void test() {
        A a = new A();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClassSimpleName(A.class);

        PathUtils pathUtils = new PathUtils(A.class,classRegistry);
        pathUtils.applyValueToPath("@ignored.(A @new(Ann)).(A @new(John)).mother(#1).father(#2).name",a,"Nick");

        assertEquals("Nick",a.name);
        assertEquals("Ann",a.mother.name);
        assertEquals("John",a.father.name);

    }

}
