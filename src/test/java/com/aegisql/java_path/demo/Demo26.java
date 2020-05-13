package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 26.
 */
public class Demo26 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The First name.
         */
        String firstName;
        /**
         * The Last name.
         */
        String lastName;
        /**
         * The Age.
         */
        int age;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("firstName; lastName; age", a, "John","Smith",55);
        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(55,a.age);
    }

    @Test
    public void testWithValues() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("firstName($0); lastName($1); age($2)", a, "John","Smith",55);
        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(55,a.age);
    }

    @Test
    public void testWithDefaultValues() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("firstName($); lastName($); age($)", a, "John","Smith",55);
        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(55,a.age);
    }

    @Test
    public void testWithInlineValues() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("firstName(John); lastName(Smith); age(int 55)", a);
        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(55,a.age);
    }

}
