package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 16.
 */
public class Demo16 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Name.
         */
        String name;

        /**
         * To upper.
         */
        public void toUpper() {
            name = name.toUpperCase();
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("name", a, "John");
        pathUtils.evalPath("toUpper()", a);
        assertEquals("JOHN",a.name);
    }

    /**
     * Test omitted parenthesis.
     */
    @Test
    public void testOmittedParenthesis () {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("name", a, "John");
        pathUtils.evalPath("toUpper", a);
        assertEquals("JOHN",a.name);
    }

}
