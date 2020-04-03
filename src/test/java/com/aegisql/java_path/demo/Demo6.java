package com.aegisql.java_path.demo;

import com.aegisql.java_path.NoLabel;
import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo6 {

    public static class A {
        StringBuilder stringBuilder = new StringBuilder();

        public void add(String str) {
            stringBuilder.append(str == null ? "N/A" : str);
        }

        @NoLabel
        public void add(Object val) {
            stringBuilder.append(val);
        }
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("add", a, null);
        assertEquals("N/A",a.stringBuilder.toString());
    }

}
