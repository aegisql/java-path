package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import com.aegisql.java_path.PathElement;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 17.
 */
public class Demo17 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Name.
         */
        String name;

        /**
         * Sets name.
         *
         * @param name the name
         */
        @PathElement("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * The type B.
     */
    public static class B {
        /**
         * The A.
         */
        A a;

        /**
         * Gets a.
         *
         * @return the a
         */
        @PathElement("a")
        public A getA() {
            if(a==null) {
                a = new A();
            }
            return a;
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("getA().setName($)", b, "John");
        assertEquals("John",b.a.name);
    }

    /**
     * Test with optional dollar sign.
     */
    @Test
    public void testWithOptionalDollarSign() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("getA().setName()", b, "John");
        assertEquals("John",b.a.name);
    }

    /**
     * Test with optional dollar sign and parenthesis.
     */
    @Test
    public void testWithOptionalDollarSignAndParenthesis() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("getA.setName", b, "John");
        assertEquals("John",b.a.name);
    }

    /**
     * Test with annotated names.
     */
    @Test
    public void testWithAnnotatedNames() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.name", b, "John");
        assertEquals("John",b.a.name);
    }

}
