package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 21.
 */
public class Demo21 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Id.
         */
        final int id;
        /**
         * The First name.
         */
        String firstName;
        /**
         * The Last name.
         */
        String lastName;

        /**
         * Instantiates a new A.
         *
         * @param name the name
         * @param id   the id
         */
        public A(String name,int id) {
            this.id = id;
            this.firstName = name;
        }

        /**
         * The Id gen.
         */
        static AtomicInteger idGen = new AtomicInteger();

        /**
         * Value of a.
         *
         * @param name the name
         * @return the a
         */
        public static A valueOf(String name) {
            A a = new A(name, idGen.incrementAndGet());
            return a;
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
        B johnSilver = new B();
        B billyBones = new B();
        ClassRegistry classRegistry = new ClassRegistry();
        JavaPath pathUtils = new JavaPath(B.class,classRegistry);
        pathUtils.evalPath("a(John).lastName", johnSilver, "Silver");
        pathUtils.evalPath("a(Billy).lastName", billyBones, "Bones");
        assertEquals("John",johnSilver.a.firstName);
        assertEquals("Silver",johnSilver.a.lastName);
        assertEquals(1,johnSilver.a.id);

        assertEquals("Billy",billyBones.a.firstName);
        assertEquals("Bones",billyBones.a.lastName);
        assertEquals(2,billyBones.a.id);

    }

}
