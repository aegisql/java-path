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
        List<TypedPathElement> path = parse("a");
        pu.applyValueToPath(path,this,"test");
        assertEquals("test",a);
    }

    @Test
    public void getRootWithATest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        List<TypedPathElement> path = parse("(com.aegisql.java_path.PathUtilsTest #).a");
        Object test = pu.initObjectFromPath(path, "test");
        assertNotNull(test);
        PathUtilsTest put = (PathUtilsTest) test;
        assertEquals("test",put.a);
    }

    private List<TypedPathElement> parse(String s) {
        return JavaPathParser.parse(s);
    }

}