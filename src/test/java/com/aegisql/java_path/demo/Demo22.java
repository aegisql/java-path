package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 22.
 */
public class Demo22 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Name.
         */
        String name;

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

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
        Object o = pathUtils.evalPath("getName", a);
        assertEquals("John",a.name);
        assertEquals("John",o);
    }

}
