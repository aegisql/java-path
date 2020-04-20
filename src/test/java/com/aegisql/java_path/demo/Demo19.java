package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class Demo19 {

    public static class A {
        Integer id = 2000;
        String desc;
        String name;

        public A setDesc(Integer id,String desc) {
            this.id = id;
            this.desc = desc;
            return this;
        }

        public A setDescRev(String desc,Integer id) {
            this.id = id;
            this.desc = desc;
            return this;
        }

    }

    public static class B {
        String desc = "fromB";
        Integer id = null;
        A a;
    }

    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.setDesc(#0.(I id),#0.(desc)).name", b, "John");
        assertEquals("John",b.a.name);
        assertNull(b.a.id);
        assertEquals("fromB",b.a.desc);
    }

    @Test
    public void testRev() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.setDescRev(#0.desc(),#0.(I id)).name", b, "John");
        assertEquals("John",b.a.name);
        assertNull(b.a.id);
        assertEquals("fromB",b.a.desc);
    }

}
