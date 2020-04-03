package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo1 {

    public static class A {
        String name;
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("name", a, "John");
        assertEquals("John",a.name);
    }

}
