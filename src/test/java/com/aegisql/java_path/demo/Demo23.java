package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 23.
 */
public class Demo23 {

    /**
     * The interface A.
     */
    interface A {
        /**
         * Gets name.
         *
         * @return the name
         */
        String getName();
    }

    /**
     * The type A.
     */
    public static class AImpl implements A {
        /**
         * The Name.
         */
        String name;

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * The type B.
     */
    public static class B {
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
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClass(AImpl.class,"AImpl");
        JavaPath pathUtils = new JavaPath(B.class,classRegistry);
        pathUtils.evalPath("(AImpl a).name", b, "John");
        assertEquals("John",b.a.getName());
    }

}
