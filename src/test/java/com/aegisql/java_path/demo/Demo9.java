package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 9.
 */
public class Demo9 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Map.
         */
        Map<String,String> map;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("(HashMap map).put(firstName)", a, "John");
        pathUtils.evalPath("map.put(lastName)", a, "Silver");
        assertEquals("John",a.map.get("firstName"));
        assertEquals("Silver",a.map.get("lastName"));
    }

    /**
     * Test with parameters.
     */
    @Test
    public void testWithParameters() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("(HashMap map(int 100,float '0.8')).put(firstName)", a, "John");
        pathUtils.evalPath("map.put(lastName)", a, "Silver");
        assertEquals("John",a.map.get("firstName"));
        assertEquals("Silver",a.map.get("lastName"));
    }

}
