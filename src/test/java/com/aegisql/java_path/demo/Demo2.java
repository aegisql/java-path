package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 2.
 */
public class Demo2 {

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
     * The type B.
     */
    public static class B {
        /**
         * The A.
         */
        A a;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.name", b, "John");
        assertEquals("John",b.a.name);
    }

    /**
     * Test with enabled caching.
     */
    @Test
    public void testWithEnabledCaching() {
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.setEnablePathCaching(true);

        B b1 = new B();
        pathUtils.evalPath("a.name", b1, "John");
        assertEquals("John",b1.a.name);

        B b2 = new B();
        pathUtils.evalPath("a.name", b2, "Mike");
        assertEquals("Mike",b2.a.name);

    }


}
