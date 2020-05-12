package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 18.
 */
public class Demo18 {

    /**
     * The type Desc.
     */
    public static class Desc {
        /**
         * The Val.
         */
        public String val;

        /**
         * Sets desc.
         *
         * @param s the s
         * @return the desc
         */
        public Desc setDesc(String s) {
            val = s;
            return this;
        }
    }

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Id.
         */
        int id;
        /**
         * The Desc.
         */
        String desc;
        /**
         * The Name.
         */
        String name;

        /**
         * Sets desc.
         *
         * @param id   the id
         * @param desc the desc
         * @return the desc
         */
        public A setDesc(int id,String desc) {
            this.id = id;
            this.desc = desc;
            return this;
        }

    }

    /**
     * The type B.
     */
    public static class B {
        /**
         * The Desc.
         */
        Desc desc = new Desc();

        {
            desc.val = "fromB";
        }

        /**
         * The Id.
         */
        int id = 1000;
        /**
         * The A.
         */
        A a;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.setDesc(#0.id,#0.desc.val).name", b, "John");
        assertEquals("John",b.a.name);
        assertEquals(1000,b.a.id);
        assertEquals("fromB",b.a.desc);
    }

    /**
     * Test with param.
     */
    @Test
    public void testWithParam() {
        B b = new B();
        JavaPath pathUtils = new JavaPath(B.class);
        pathUtils.evalPath("a.setDesc(#0.id,#0.desc.setDesc(fromTest).val).name", b, "John");
        assertEquals("John",b.a.name);
        assertEquals(1000,b.a.id);
        assertEquals("fromTest",b.a.desc);
    }

}
