package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import com.aegisql.java_path.PathElement;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo17 {

    public static class A {
        String name;

        @PathElement("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class B {
        A a;

        @PathElement("a")
        public A getA() {
            if(a==null) {
                a = new A();
            }
            return a;
        }
    }

    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("getA().setName($)", b, "John");
        assertEquals("John",b.a.name);
    }

    @Test
    public void testWithOptionalDollarSign() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("getA().setName()", b, "John");
        assertEquals("John",b.a.name);
    }

    @Test
    public void testWithOptionalDollarSignAndParenthesis() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("getA.setName", b, "John");
        assertEquals("John",b.a.name);
    }

    @Test
    public void testWithAnnotatedNames() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.applyValueToPath("a.name", b, "John");
        assertEquals("John",b.a.name);
    }

}
