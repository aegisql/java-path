package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 25.
 */
public class Demo25 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Map.
         */
        HashMap<String,Object> map;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();
        Supplier<AtomicInteger> supplier = AtomicInteger::new;

        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("map.put(name)", a, "John");
        pathUtils.evalPath("map.put(msg)", a, new StringBuilder());

        pathUtils.evalPath("map.get(msg).append('Dear ').append(#1.get(name)).append", a, "!");
        pathUtils.evalPath("map.get(counter)||put(counter,$1.get).getAndAdd", a, 100,supplier);
        pathUtils.evalPath("map.get(counter)||put(counter,$1.get).getAndAdd", a, 100,supplier);

        assertEquals("John",a.map.get("name"));
        assertEquals("Dear John!",a.map.get("msg").toString());
        assertEquals("200",a.map.get("counter").toString());

        System.out.println(a.map);
    }

}
