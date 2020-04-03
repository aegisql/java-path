package com.aegisql.java_path.demo;

import com.aegisql.java_path.Label;
import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo4 {

    public static class A {
        String name;

        @Label("first_name")
        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("first_name", a, "John");
        assertEquals("John",a.name);
    }

}
