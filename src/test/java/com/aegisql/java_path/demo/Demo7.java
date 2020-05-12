package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 7.
 */
public class Demo7 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The String builder.
         */
        StringBuilder stringBuilder;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("stringBuilder(John).append(' ').append", a, "Silver");
        assertEquals("John Silver",a.stringBuilder.toString());
    }

}
