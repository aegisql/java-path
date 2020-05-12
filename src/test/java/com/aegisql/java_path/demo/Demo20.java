package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 20.
 */
public class Demo20 {

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
         * New a a.
         *
         * @param id   the id
         * @param name the name
         * @return the a
         */
        public static A newA(int id, String name) {
            A a = new A(name, id);
            return a;
        }

        /**
         * New a a.
         *
         * @param name the name
         * @return the a
         */
        public static A newA(String name) {
            A a = new A(name, idGen.incrementAndGet());
            return a;
        }

        /**
         * New default a a.
         *
         * @return the a
         */
        public static A newDefaultA() {
            A a = new A("anonymous", idGen.incrementAndGet());
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
        classRegistry.registerStringConverter(A.class,A::newA);
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

    /**
     * Test default.
     */
    @Test
    public void testDefault() {
        B johnSilver = new B();
        B billyBones = new B();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerStringConverter(A.class,s->A.newDefaultA());
        JavaPath pathUtils = new JavaPath(B.class,classRegistry);
        pathUtils.evalPath("a(John).lastName", johnSilver, "Silver");
        pathUtils.evalPath("a(Billy).lastName", billyBones, "Bones");
        assertEquals("anonymous",johnSilver.a.firstName);
        assertEquals("Silver",johnSilver.a.lastName);
        assertEquals(3,johnSilver.a.id);

        assertEquals("anonymous",billyBones.a.firstName);
        assertEquals("Bones",billyBones.a.lastName);
        assertEquals(4,billyBones.a.id);
    }

    /**
     * Test factory.
     */
    @Test
    public void testFactory() {
        B johnSilver = new B();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClass(A.class,"A");
        JavaPath pathUtils = new JavaPath(B.class,classRegistry);
        pathUtils.evalPath("(A::newA a(int 1, John)).lastName", johnSilver, "Silver");
        assertEquals("John",johnSilver.a.firstName);
        assertEquals("Silver",johnSilver.a.lastName);
        assertEquals(1,johnSilver.a.id);

    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        B johnSilver = new B();
        ClassRegistry classRegistry = new ClassRegistry();
        classRegistry.registerClass(A.class,"A");
        JavaPath pathUtils = new JavaPath(B.class,classRegistry);
        pathUtils.evalPath("(A::new a(John, int 1)).lastName", johnSilver, "Silver");
        assertEquals("John",johnSilver.a.firstName);
        assertEquals("Silver",johnSilver.a.lastName);
        assertEquals(1,johnSilver.a.id);

    }

}
