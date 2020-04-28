package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Demo23 {

    interface A {
        String getName();
    }

    public static class AImpl implements A {
        String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public static class B {
        A a;
    }

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
