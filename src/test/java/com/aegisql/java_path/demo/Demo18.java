package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo18 {

    public static class A {
        int id;
        String desc;
        String name;

        public A setDesc(int id,String desc) {
            this.id = id;
            this.desc = desc;
            return this;
        }

    }

    public static class B {
        String desc = "fromB";
        int id = 1000;
        A a;
    }

    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("a.setDesc(#0.id,#0.desc).name", b, "John");
        assertEquals("John",b.a.name);
        assertEquals(1000,b.a.id);
        assertEquals("fromB",b.a.desc);
    }

}
