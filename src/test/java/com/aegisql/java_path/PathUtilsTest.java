package com.aegisql.java_path;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PathUtilsTest {

    private String a;

    @PathElement("upper")
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
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        pu.evalPath("a",this,"test");
        assertEquals("test",a);
    }

    @Test
    public void setUpperATest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        Object o = pu.evalPath("upper",this,"test");
        assertEquals("test",a);
        assertEquals("TEST",o);
    }

    @Test
    public void getRootWithATest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        Object test = pu.initPath("(com.aegisql.java_path.PathUtilsTest #0).a", "test");
        assertNotNull(test);
        PathUtilsTest put = (PathUtilsTest) test;
        assertEquals("test",put.a);
    }

    @Test
    public void getRootWithClassTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        PathUtilsTest test = pu.initPath("#.a",PathUtilsTest.class, "test");
        assertNotNull(test);
        assertEquals("test",test.a);
    }

    @Test
    public void getRootWithClassAndAtTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        PathUtilsTest test = pu.initPath("#.@this_is_a.a",PathUtilsTest.class, "test");
        assertNotNull(test);
        assertEquals("test",test.a);
    }

    @Test(expected = JavaPathRuntimeException.class)
    public void getRootWithClassShouldFailTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        //cannot refer to #1
        PathUtilsTest test = pu.initPath("#1.a",PathUtilsTest.class, "test");
    }

    @Test
    public void stringBuilderAppendTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).append(c).append(int $)",sb,100);

        assertEquals("abc100",sb.toString());
    }

    @Test
    public void backRefTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).(string @new(TESTING)).append(#1).append(int $)",sb,100);
        assertEquals("abTESTING100",sb.toString());
    }

    @Test
    public void backRefFactoryTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).(Integer @valueOf(100)).append(#1).append(int $)",sb,100);
        assertEquals("ab100100",sb.toString());
    }

    @Test
    public void initWithMultipleParametersTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = pu.initPath("#.append($0).append($1).append($2).append($3)", StringBuilder.class, "ab","cd","dc","ba");
        assertEquals("abcddcba",sb.toString());
    }

    @Test
    public void initWithMultipleParametersNoClassTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = (StringBuilder) pu.initPath("("+StringBuilder.class.getName()+" #).append($0).append($1).append($2).append($3)", "ab","cd","dc","ba");
        assertEquals("abcddcba",sb.toString());
    }

    @Test
    public void initWithMultipleParametersAndFieldsNoClassTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = (StringBuilder) pu.initPath("("+StringBuilder.class.getName()+" #).append($0.substring(int 1)).append($1).append($2).append($3.substring(int 1,int 3))", "zab","cd","dc","zbaz");
        assertEquals("abcddcba",sb.toString());
    }

    public static class AS {
        private String val;

        public String getVal() {
            return val;
        }

        public static void setVal(AS b, String val) {
            b.val = val;
        }
    }

    @Test
    public void testAS() {
        AS a = new AS();
        JavaPath pu = new JavaPath(AS.class);
        Object as = pu.evalPath("setVal(#)", a,"test");
        assertEquals("test",a.val);
    }

    public static class PC {
        PC parent;
        PC child;
        String a;
        public PC(PC parent) {
            this.parent = parent;
        }
    }

    @Test
    public void parentChildTest() {
        JavaPath pu = new JavaPath(PC.class);
        PC root = new PC(null);
        pu.evalPath("a",root,"PARENT");
        pu.evalPath("child(#).a",root,"CHILD");
        pu.evalPath("child(#).child(#1).a",root,"GRAND-CHILD");
        assertNull(root.parent);
        assertNotNull(root.child);
        assertNotNull(root.child.parent);
        assertNotNull(root.child.child);
        assertNotNull(root.child.child.parent);
        assertEquals("PARENT",root.a);
        assertEquals("CHILD",root.child.a);
        assertEquals("GRAND-CHILD",root.child.child.a);
        assertEquals("CHILD",root.child.child.parent.a);
        assertEquals("PARENT",root.child.child.parent.parent.a);
    }

    public static class A {
        int sum = 0;
        public void add(int x) {
            sum += x;
        }
    }

    @Test
    public void testPrimitiveTypeCasting() {

        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a, 100);


        assertEquals(100,a.sum);
    }

}