package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo2 {

    public static class A {
        String name;
    }

    public static class B {
        A a;
    }

    @Test
    public void test() {
        B b = new B();
        PathUtils pathUtils = new PathUtils(B.class);
        pathUtils.applyValueToPath("a.name", b, "John");
        assertEquals("John",b.a.name);
    }

}
