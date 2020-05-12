package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 5.
 */
public class Demo5 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The String builder.
         */
        StringBuilder stringBuilder = new StringBuilder();
        /**
         * The Sum.
         */
        int sum = 0;

        /**
         * Add a.
         *
         * @param str the str
         * @return the a
         */
        public A add(String str) {
            stringBuilder.append(str);
            return this;
        }

        /**
         * Add a.
         *
         * @param x the x
         * @return the a
         */
        public A add(int x) {
            sum += x;
            return this;
        }

        /**
         * Str to int int.
         *
         * @param val the val
         * @return the int
         */
        public static int strToInt(String val) {
            return Integer.valueOf(val).intValue();
        }

    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a, "John");
        assertEquals("John",a.stringBuilder.toString());
        pathUtils.evalPath("add", a, 100);
        assertEquals(100,a.sum);
    }

    /**
     * Test factory.
     */
    @Test
    public void testFactory() {
        A a = new A();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClass(A.class,"A");
        JavaPath pathUtils = new JavaPath(A.class,classRegistry);
        pathUtils.evalPath("add(A::strToInt 1000).add", a, 100);
        assertEquals(1100,a.sum);
    }

}
