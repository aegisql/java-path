package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * The type Demo 19.
 */
public class Demo19 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Id.
         */
        Integer id = 2000;
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
        public A setDesc(Integer id,String desc) {
            this.id = id;
            this.desc = desc;
            return this;
        }

        /**
         * Sets desc rev.
         *
         * @param desc the desc
         * @param id   the id
         * @return the desc rev
         */
        public A setDescRev(String desc,Integer id) {
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
        String desc = "fromB";
        /**
         * The Id.
         */
        Integer id = null;
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
        pathUtils.evalPath("a.setDesc(#0.(I id),#0.(desc)).name", b, "John");
        assertEquals("John",b.a.name);
        assertNull(b.a.id);
        assertEquals("fromB",b.a.desc);
    }

    /**
     * Test rev.
     */
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
