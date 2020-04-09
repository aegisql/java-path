package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo3 {

    public static class A {
        String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.applyValueToPath("setName", a, "John");
        assertEquals("John",a.name);
    }

}
