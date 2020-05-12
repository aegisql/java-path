package com.aegisql.java_path;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The type Path utils test.
 */
public class PathUtilsTest {

    private String a;
    private String b;

    /**
     * Sets upper a.
     *
     * @param s the s
     * @return the upper a
     */
    @PathElement("upper")
    String setUpperA( String s) {
        a = s;
        return a.toUpperCase();
    }

    /**
     * Init.
     */
    @Before
    public void init() {
        a = null;
        b = null;
    }

    /**
     * Sets a test.
     */
    @Test
    public void setATest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        pu.evalPath("a",this,"test");
        assertEquals("test",a);
    }

    /**
     * Sets upper a test.
     */
    @Test
    public void setUpperATest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        Object o = pu.evalPath("upper",this,"test");
        assertEquals("test",a);
        assertEquals("TEST",o);
    }

    /**
     * Gets root with a test.
     */
    @Test
    public void getRootWithATest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        Object test = pu.initPath("(com.aegisql.java_path.PathUtilsTest #0).a", "test");
        assertNotNull(test);
        PathUtilsTest put = (PathUtilsTest) test;
        assertEquals("test",put.a);
    }

    /**
     * Gets root with class test.
     */
    @Test
    public void getRootWithClassTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        PathUtilsTest test = pu.initPath("#.a",PathUtilsTest.class, "test");
        assertNotNull(test);
        assertEquals("test",test.a);
    }

    /**
     * Gets root with class and at test.
     */
    @Test
    public void getRootWithClassAndAtTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        PathUtilsTest test = pu.initPath("#.@this_is_a.a",PathUtilsTest.class, "test");
        assertNotNull(test);
        assertEquals("test",test.a);
    }

    /**
     * Gets root with class should fail test.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void getRootWithClassShouldFailTest() {
        JavaPath pu = new JavaPath(PathUtilsTest.class);
        //cannot refer to #1
        PathUtilsTest test = pu.initPath("#1.a",PathUtilsTest.class, "test");
    }

    /**
     * String builder append test.
     */
    @Test
    public void stringBuilderAppendTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).append(c).append(int $)",sb,100);

        assertEquals("abc100",sb.toString());
    }

    /**
     * Back ref test.
     */
    @Test
    public void backRefTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).(string @new(TESTING)).append(#1).append(int $)",sb,100);
        assertEquals("abTESTING100",sb.toString());
    }

    /**
     * Back ref factory test.
     */
    @Test
    public void backRefFactoryTest() {
        StringBuilder sb = new StringBuilder();
        JavaPath pu = new JavaPath(StringBuilder.class);
        pu.evalPath("append(a).append(b).(Integer @valueOf(100)).append(#1).append(int $)",sb,100);
        assertEquals("ab100100",sb.toString());
    }

    /**
     * Init with multiple parameters test.
     */
    @Test
    public void initWithMultipleParametersTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = pu.initPath("#.append($0).append($1).append($2).append($3)", StringBuilder.class, "ab","cd","dc","ba");
        assertEquals("abcddcba",sb.toString());
    }

    /**
     * Init with multiple parameters no class test.
     */
    @Test
    public void initWithMultipleParametersNoClassTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = (StringBuilder) pu.initPath("("+StringBuilder.class.getName()+" #).append($0).append($1).append($2).append($3)", "ab","cd","dc","ba");
        assertEquals("abcddcba",sb.toString());
    }

    /**
     * Init with multiple parameters and fields no class test.
     */
    @Test
    public void initWithMultipleParametersAndFieldsNoClassTest() {
        JavaPath pu = new JavaPath(StringBuilder.class);
        StringBuilder sb = (StringBuilder) pu.initPath("("+StringBuilder.class.getName()+" #).append($0.substring(int 1)).append($1).append($2).append($3.substring(int 1,int 3))", "zab","cd","dc","zbaz");
        assertEquals("abcddcba",sb.toString());
    }

    /**
     * The type As.
     */
    public static class AS {
        private String val;

        /**
         * Gets val.
         *
         * @return the val
         */
        public String getVal() {
            return val;
        }

        /**
         * Sets val.
         *
         * @param b   the b
         * @param val the val
         */
        public static void setVal(AS b, String val) {
            b.val = val;
        }
    }

    /**
     * Test as.
     */
    @Test
    public void testAS() {
        AS a = new AS();
        JavaPath pu = new JavaPath(AS.class);
        Object as = pu.evalPath("setVal(#)", a,"test");
        assertEquals("test",a.val);
    }

    /**
     * The type Pc.
     */
    public static class PC {
        /**
         * The Parent.
         */
        PC parent;
        /**
         * The Child.
         */
        PC child;
        /**
         * The A.
         */
        String a;

        /**
         * Instantiates a new Pc.
         *
         * @param parent the parent
         */
        public PC(PC parent) {
            this.parent = parent;
        }
    }

    /**
     * Parent child test.
     */
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

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Sum.
         */
        int sum = 0;

        /**
         * Add.
         *
         * @param x the x
         */
        public void add(int x) {
            sum += x;
        }
    }

    /**
     * Test primitive type casting.
     */
    @Test
    public void testPrimitiveTypeCasting() {

        A a = new A();
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("add", a, 100);


        assertEquals(100,a.sum);
    }

    /**
     * The type C.
     */
    class C {
        /**
         * The Sb.
         */
        StringBuilder sb;

        /**
         * Init.
         *
         * @param str the str
         */
        public void init(String str) {
            sb = new StringBuilder(str);
        }

        /**
         * Init.
         */
        public void init() {
            sb = new StringBuilder();
        }

        /**
         * Gets sb.
         *
         * @return the sb
         */
        public StringBuilder getSb() {
            return sb;
        }

        /**
         * Sets sb.
         *
         * @param sb the sb
         */
        public void setSb(StringBuilder sb) {
            this.sb = sb;
        }
    }

    /**
     * Test init choice.
     */
    @Test
    public void testInitChoice() {
        C c = new C();
        JavaPath pathUtils = new JavaPath(C.class);
        pathUtils.evalPath("getSb||init('Dear Mr. ').append(John).append", c, " Silver");
        assertNotNull(c.sb);
        assertEquals("Dear Mr. John Silver",c.sb.toString());
    }

    /**
     * Test init choice 2.
     */
    @Test
    public void testInitChoice2() {
        C c = new C();
        JavaPath pathUtils = new JavaPath(C.class);
        pathUtils.evalPath("getSb||setSb($0).append($1).append(' ').append($2)", c, new StringBuilder("Dear Mr. "),"John","Silver");
        pathUtils.evalPath("getSb||setSb($0).append(', ').append($1)", c, new StringBuilder("Dear Mr. "),"the pirate");
        assertNotNull(c.sb);
        assertEquals("Dear Mr. John Silver, the pirate",c.sb.toString());
    }

    /**
     * Test init choice 3.
     */
    @Test
    public void testInitChoice3() {
        C c = new C();
        JavaPath pathUtils = new JavaPath(C.class);
        pathUtils.evalPath("getSb||init.append(John).append", c, " Silver");
        assertNotNull(c.sb);
        assertEquals("John Silver",c.sb.toString());
    }

    /**
     * Test init choice 4.
     */
    @Test
    public void testInitChoice4() {
        C c = new C();
        JavaPath pathUtils = new JavaPath(C.class);
        pathUtils.evalPath("getSb||setSb($1.get).append(John).append", c, " Silver",(Supplier)()->new StringBuilder("Dear Mr. "));
        assertNotNull(c.sb);
        assertEquals("Dear Mr. John Silver",c.sb.toString());
    }

    /**
     * Ab test with params.
     */
    @Test
    public void abTestWithParams() {
        JavaPath javaPath = new JavaPath(this.getClass());
        javaPath.evalPath("a($0);b($1)",this,"A","B");
        assertEquals("A",a);
        assertEquals("B",b);
    }

    /**
     * Ab test.
     */
    @Test
    public void abTest() {
        JavaPath javaPath = new JavaPath(this.getClass());
        javaPath.evalPath("a;b",this,"A","B");
        assertEquals("A",a);
        assertEquals("B",b);
    }

    /**
     * Ab new line test.
     */
    @Test
    public void abNewLineTest() {
        JavaPath javaPath = new JavaPath(this.getClass());
        javaPath.evalPath("a\nb",this,"A","B");
        assertEquals("A",a);
        assertEquals("B",b);
    }

}