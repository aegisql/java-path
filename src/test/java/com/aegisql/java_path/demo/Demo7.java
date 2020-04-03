package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo7 {

    public static class A {
        StringBuilder stringBuilder;
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("stringBuilder{John}.append{' '}.append", a, "Silver");
        assertEquals("John Silver",a.stringBuilder.toString());
    }

}
