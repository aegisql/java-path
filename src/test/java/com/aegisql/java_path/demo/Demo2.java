package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
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
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("a.name", b, "John");
        assertEquals("John",b.a.name);
    }

    @Test
    public void testWithEnabledCaching() {
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.setEnableCaching(true);

        B b1 = new B();
        pathUtils.applyValueToPath("a.name", b1, "John");
        assertEquals("John",b1.a.name);

        B b2 = new B();
        pathUtils.applyValueToPath("a.name", b2, "Mike");
        assertEquals("Mike",b2.a.name);

    }


}
