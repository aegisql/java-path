package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 3.
 */
public class Demo3 {

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
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("setName", a, "John");
        assertEquals("John",a.name);
    }

}
