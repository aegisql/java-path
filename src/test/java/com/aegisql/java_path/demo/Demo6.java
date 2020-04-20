package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import com.aegisql.java_path.NoPathElement;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo6 {

    public static class A {
        StringBuilder stringBuilder = new StringBuilder();

        public void add(String str) {
            stringBuilder.append(str == null ? "N/A" : str);
        }

        @NoPathElement
        public void add(Object val) {
            stringBuilder.append(val);
        }
    }

    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a);
        assertEquals("N/A",a.stringBuilder.toString());
    }

}
