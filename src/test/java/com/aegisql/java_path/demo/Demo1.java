package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 1.
 */
public class Demo1 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Name.
         */
        String name;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("name", a, "John");
        assertEquals("John",a.name);
    }

}
