package com.aegisql.java_path;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PathUtilsTest {

    private String a;

    @Label("upper")
    String setUpperA( String s) {
        a = s;
        return a.toUpperCase();
    }

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
    public void setUpperATest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        Object o = pu.applyValueToPath("upper",this,"test");
        assertEquals("test",a);
        assertEquals("TEST",o);
    }

    @Test
    public void getRootWithATest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        Object test = pu.initObjectFromPath("(com.aegisql.java_path.PathUtilsTest #0).a", "test");
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

    @Test(expected = JavaPathRuntimeException.class)
    public void getRootWithClassShouldFailTest() {
        PathUtils pu = new PathUtils(PathUtilsTest.class);
        //cannot refer to #1
        PathUtilsTest test = pu.initObjectFromPath("#1.a",PathUtilsTest.class, "test");
    }

    @Test
    public void stringBuilderAppendTest() {
        StringBuilder sb = new StringBuilder();
        PathUtils pu = new PathUtils(StringBuilder.class);
        pu.applyValueToPath("append{a}.append{b}.append{c}.append{int $}",sb,100);

        assertEquals("abc100",sb.toString());
    }

    private List<TypedPathElement> parse(String s) {
        return JavaPathParser.parse(s);
    }

}