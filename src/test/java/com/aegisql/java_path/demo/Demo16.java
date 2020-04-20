package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo16 {

    public static class A {
        String name;
        public void toUpper() {
            name = name.toUpperCase();
        }
    }

    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("name", a, "John");
        pathUtils.evalPath("toUpper()", a);
        assertEquals("JOHN",a.name);
    }

    @Test
    public void testOmittedParenthesis () {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("name", a, "John");
        pathUtils.evalPath("toUpper", a);
        assertEquals("JOHN",a.name);
    }

}
