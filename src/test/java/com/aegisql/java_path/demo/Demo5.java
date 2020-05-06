package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo5 {

    public static class A {
        StringBuilder stringBuilder = new StringBuilder();
        int sum = 0;

        public A add(String str) {
            stringBuilder.append(str);
            return this;
        }

        public A add(int x) {
            sum += x;
            return this;
        }
    }

    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a, "John");
        assertEquals("John",a.stringBuilder.toString());
        pathUtils.evalPath("add", a, 100);
        assertEquals(100,a.sum);
    }

    @Test
    public void testFactory() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add(Integer::valueOf 1000).add", a, 100);
        assertEquals(1100,a.sum);
    }

}
