package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import com.aegisql.java_path.JavaPathRuntimeException;
import com.aegisql.java_path.NoPathElement;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 6.
 */
public class Demo6 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The String builder.
         */
        StringBuilder stringBuilder = new StringBuilder();

        /**
         * The Protected field.
         */
        @NoPathElement
        String protectedField = "IMMUTABLE";

        /**
         * Add.
         *
         * @param str the str
         */
        public void add(String str) {
            stringBuilder.append(str == null ? "N/A" : str);
        }

        /**
         * Add.
         *
         * @param val the val
         */
        @NoPathElement
        public void add(Object val) {
            stringBuilder.append(val);
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a);
        assertEquals("N/A",a.stringBuilder.toString());
    }

    /**
     * Protected field should fail.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void protectedFieldShouldFail() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("protectedField", a,"CHANGED!");
    }

}
