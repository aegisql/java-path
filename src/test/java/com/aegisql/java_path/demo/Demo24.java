package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 24.
 */
public class Demo24 {

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
        pathUtils.evalPath("stringBuilder.append($0.substring(i 1)).append(' ').append($2).append($3.substring(i 0,i 6))", a, " John"," ","Silver ","pirate and cook");
        assertEquals("John Silver pirate",a.stringBuilder.toString());
    }

}
