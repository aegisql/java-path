package com.aegisql.java_path;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PathUtilsTest {

    private String a;

    @Before
    public void init() {
        a = null;
    }

    @Test
    public void setATest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        pu.applyValueToPath("a",this,"test");
        assertEquals("test",a);
    }

    @Test
    public void getRootWithATest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        Object test = pu.initObjectFromPath("(com.aegisql.java_path.PathUtilsTest #).a", "test");
        assertNotNull(test);
        PathUtilsTest put = (PathUtilsTest) test;
        assertEquals("test",put.a);
    }

    @Test
    public void getRootWithClassTest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        PathUtilsTest test = pu.initObjectFromPath("#.a",PathUtilsTest.class, "test");
        assertNotNull(test);
        assertEquals("test",test.a);
    }

    private List<TypedPathElement> parse(String s) {
        return JavaPathParser.parse(s);
    }

}