package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

public class Demo20 {

    public static class A {
        final int id;
        String firstName;
        String lastName;

        public A(String name,int id) {
            this.id = id;
            this.firstName = name;
        }

        static AtomicInteger idGen = new AtomicInteger();

        public static A newA(String name) {
            A a = new A(name, idGen.incrementAndGet());
            return a;
        }

    }

    public static class B {
        A a;
    }

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

}
