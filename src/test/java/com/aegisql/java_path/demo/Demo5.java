package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo5 {

    public static class A {
        StringBuilder stringBuilder = new StringBuilder();
        int sum = 0;

        public void add(String str) {
            stringBuilder.append(str);
        }

        public void add(int x) {
            sum += x;
        }
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("add", a, "John");
        assertEquals("John",a.stringBuilder.toString());
        pathUtils.applyValueToPath("add", a, 100);
        assertEquals(100,a.sum);
    }

}
