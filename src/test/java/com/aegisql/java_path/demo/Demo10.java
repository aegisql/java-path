package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 10.
 */
public class Demo10 {

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
        pathUtils.evalPath("(HashMap map).put($,firstName)", a, "John");
        pathUtils.evalPath("map.put($,lastName)", a, "Silver");
        assertEquals("firstName",a.map.get("John"));
        assertEquals("lastName",a.map.get("Silver"));
    }

    /**
     * Test multi path.
     */
    @Test
    public void testMultiPath() {
        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("map;map.put(firstName,$1);map.put(lastName,$2);", a, new HashMap<>(),"John","Silver");
        System.out.println(a.map);
        assertEquals("John",a.map.get("firstName"));
        assertEquals("Silver",a.map.get("lastName"));
    }

}
