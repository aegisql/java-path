package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo26 {

    public static class A {
        String firstName;
        String lastName;
    }

    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("firstName;lastName;", a, "John","Smith");
        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
    }

}
