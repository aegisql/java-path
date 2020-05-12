package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import com.aegisql.java_path.PathElement;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 4.
 */
public class Demo4 {

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
        @PathElement({"first_name"})
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
        pathUtils.evalPath("first_name", a, "John");
        assertEquals("John",a.name);
    }

}
