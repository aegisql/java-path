package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class Demo10 {

    public static class A {
        Map<String,String> map;
    }

    @Test
    public void test() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("(HashMap map).put{$,firstName}", a, "John");
        pathUtils.applyValueToPath("map.put{$,lastName}", a, "Silver");
        assertEquals("firstName",a.map.get("John"));
        assertEquals("lastName",a.map.get("Silver"));
    }

}
