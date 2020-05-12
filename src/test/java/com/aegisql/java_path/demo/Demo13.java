package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 13.
 */
public class Demo13 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Mother.
         */
        A mother;
        /**
         * The Father.
         */
        A father;
        /**
         * The Name.
         */
        String name;

        /**
         * Instantiates a new A.
         */
        public A() {}

        /**
         * Instantiates a new A.
         *
         * @param name the name
         */
        public A(String name) {
            this.name = name;
        }

        /**
         * Mother a.
         *
         * @param mother the mother
         * @return the a
         */
        public A mother(A mother) {
            this.mother = mother;
            return this;
        }

        /**
         * Father a.
         *
         * @param father the father
         * @return the a
         */
        public A father(A father) {
            this.father = father;
            return this;
        }

    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClass(A.class,A.class.getSimpleName());

        JavaPath pathUtils = new JavaPath(A.class,classRegistry);
        pathUtils.evalPath("@ignored.(A @new(Ann)).(A @new(John)).mother(#1).father(#2).name",a,"Nick");

        assertEquals("Nick",a.name);
        assertEquals("Ann",a.mother.name);
        assertEquals("John",a.father.name);

    }

}
