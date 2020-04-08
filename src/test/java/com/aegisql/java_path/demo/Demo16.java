package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
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
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("name", a, "John");
        pathUtils.applyValueToPath("toUpper()", a,null);
        assertEquals("JOHN",a.name);
    }

    @Test
    public void testOmittedParenthesis () {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("name", a, "John");
        pathUtils.applyValueToPath("toUpper", a,null);
        assertEquals("JOHN",a.name);
    }

}