package com.aegisql.java_path;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * The type Call tree test.
 */
public class CallTreeTest {

    /**
     * The type X.
     */
    class X{
        /**
         * X.
         */
        @PathElement("a")
        @NoPathElement
        void x(){}
    }

    /**
     * The A.
     */
    String a = "test";

    /**
     * A.
     */
    void a(){}

    /**
     * A.
     *
     * @param x the x
     */
    void a(String x){}

    /**
     * A.
     *
     * @param x the x
     * @param y the y
     */
    void a(String x, String y){}

    /**
     * B.
     *
     * @param x the x
     */
    @PathElement({"bee","bi"})
    void b(String x){}

    /**
     * O.
     *
     * @param x the x
     */
    void o(CharSequence x){}

    /**
     * O.
     *
     * @param x the x
     * @param y the y
     */
    void o(CharSequence x,CharSequence y){}

    /**
     * O.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    void o(CharSequence x,CharSequence y,CharSequence z){}

    /**
     * No label.
     */
    @NoPathElement
    void noLabel(){};

    /**
     * Basic test.
     */
    @Test
    public void basicTest() {
        CallTree mt = new CallTree();
        mt.addMethod(getMethod("a"));
        mt.addMethod(getMethod("a",new Class[]{String.class}));
        mt.addMethod(getMethod("b",new Class[]{String.class}));
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class,CharSequence.class}));
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class}));
        mt.addMethod(getMethod("a",new Class[]{String.class, String.class}));
        System.out.println(mt);

        Method a = mt.findMethod("a");
        System.out.println(a);
        assertNotNull(a);
        assertEquals("a",a.getName());
        assertEquals(0,a.getParameterCount());

        Method aS = mt.findMethod("a",new Class[]{String.class});
        System.out.println(aS);
        assertNotNull(aS);
        assertEquals("a",aS.getName());
        assertEquals(1,aS.getParameterCount());

        Method aSS = mt.findMethod("a",new Class[]{String.class,String.class});
        System.out.println(aSS);
        assertNotNull(aSS);
        assertEquals("a",aSS.getName());
        assertEquals(2,aSS.getParameterCount());

        Method bS = mt.findMethod("b",new Class[]{String.class});
        System.out.println(bS);
        assertNotNull(bS);
        assertEquals("b",bS.getName());
        assertEquals(1,bS.getParameterCount());
        assertEquals(bS,mt.findMethod("bee",new Class[]{String.class}));
        assertEquals(bS,mt.findMethod("bi",new Class[]{String.class}));

        Method oS = mt.findMethod("o",new Class[]{String.class});
        System.out.println(oS);
        assertNotNull(oS);
        assertEquals("o",oS.getName());
        assertEquals(1,oS.getParameterCount());

        Method oSS = mt.findMethod("o",new Class[]{String.class,String.class});
        System.out.println(oSS);
        assertNotNull(oSS);
        assertEquals("o",oSS.getName());
        assertEquals(2,oSS.getParameterCount());

        Set<Method> aSSL = mt.findMethodCandidates("a",new Class[]{String.class,null});
        System.out.println(aSSL);
        assertNotNull(aSSL);
        assertEquals(1, aSSL.size());
        Method aSS2 = aSSL.iterator().next();
        assertNotNull(aSS2);
        assertEquals(aSS,aSS2);

        Set<Method> oSN = mt.findMethodCandidates("o",new Class[]{null});
        System.out.println(oSN);
        assertNotNull(oSN);
        Method next = oSN.iterator().next();
        assertEquals("o",next.getName());
        assertEquals(1,next.getParameterCount());

        System.out.println(mt);
    }

    /**
     * Find method candidates.
     */
    @Test
    public void findMethodCandidates() {
        CallTree mt = CallTree.forClass(StringBuilder.class);
        Set<Method> candidates = mt.findMethodCandidates("append");
        assertNotNull(candidates);
        assertEquals(12,candidates.size());
    }

    /**
     * Test class lookup.
     */
    @Test
    public void testClassLookup() {
        CallTree mt = CallTree.forClass(this.getClass());
        System.out.println(mt);
        Method a = mt.findMethod("a");
        System.out.println(a);
        assertNotNull(a);
        assertEquals("a",a.getName());
        assertEquals(0,a.getParameterCount());

        Method aS = mt.findMethod("a",new Class[]{String.class});
        System.out.println(aS);
        assertNotNull(aS);
        assertEquals("a",aS.getName());
        assertEquals(1,aS.getParameterCount());

        Method aSS = mt.findMethod("a",new Class[]{String.class,String.class});
        System.out.println(aSS);
        assertNotNull(aSS);
        assertEquals("a",aSS.getName());
        assertEquals(2,aSS.getParameterCount());

        Method bS = mt.findMethod("b",new Class[]{String.class});
        System.out.println(bS);
        assertNotNull(bS);
        assertEquals("b",bS.getName());
        assertEquals(1,bS.getParameterCount());

        Method oS = mt.findMethod("o",new Class[]{String.class});
        System.out.println(oS);
        assertNotNull(oS);
        assertEquals("o",oS.getName());
        assertEquals(1,oS.getParameterCount());

        Set<Method> aSSL = mt.findMethodCandidates("a",new Class[]{String.class,null});
        System.out.println(aSSL);
        assertNotNull(aSSL);
        assertEquals(1,aSSL.size());
        Method aSS2 = aSSL.iterator().next();
        assertNotNull(aSS2);
        assertEquals(aSS,aSS2);

        Set<Method> oSN = mt.findMethodCandidates("o",new Class[]{null});
        System.out.println(oSN);
        assertNotNull(oSN);
        Method next = oSN.iterator().next();
        assertEquals("o",next.getName());
        assertEquals(1,next.getParameterCount());

        Method nl = mt.findMethod("noLabel");
        assertNull(nl);

        Method n = mt.findMethod("z");
        assertNull(n);

        Set<Method> z = mt.findMethodCandidates("z",new Class[]{String.class,null});
        assertEquals(0,z.size());

    }

    /**
     * O candidates test.
     */
    @Test
    public void oCandidatesTest() {
        CallTree mt = new CallTree();
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class,CharSequence.class}));
        Set<Method> oSSn = mt.findMethodCandidates("o",new Class[]{String.class,null});
        System.out.println(oSSn);
        assertNotNull(oSSn);
        assertEquals("o",oSSn.iterator().next().getName());

    }

    /**
     * Find candidates for void.
     */
    @Test
    public void findCandidatesForVoid() {
        CallTree mt = CallTree.forClass(this.getClass());
        Set<Method> as = mt.findMethodCandidates("a");
        assertNotNull(as);
        assertEquals(1,as.size());
        Method next = as.iterator().next();
        assertEquals("a",next.getName());
        assertEquals(0,next.getParameterCount());
    }

    /**
     * Oo candidates test.
     */
    @Test
    public void ooCandidatesTest() {
        CallTree mt = new CallTree();
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class,CharSequence.class,CharSequence.class}));
        Set<Method> oSSn = mt.findMethodCandidates("o",new Class[]{String.class,String.class,null});
        System.out.println(oSSn);
        assertNotNull(oSSn);
        assertEquals("o",oSSn.iterator().next().getName());
    }

    /**
     * O candidates test fail.
     */
    @Test
    public void oCandidatesTestFail() {
        CallTree mt = new CallTree();
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class,CharSequence.class}));
        Set<Method> oSSn = mt.findMethodCandidates("o",new Class[]{Integer.class,null});
        assertEquals(0,oSSn.size());
    }

    /**
     * Oo candidates test fail.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void ooCandidatesTestFail() {
        CallTree mt = new CallTree();
        mt.addMethod(getMethod("o",new Class[]{CharSequence.class,CharSequence.class,CharSequence.class}));
        Set<Method> oSSn = mt.findMethodCandidates("o",new Class[]{String.class,Integer.class,null});
    }

    /**
     * Class lookup should fail on x.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void classLookupShouldFailOnX(){
        CallTree mt = CallTree.forClass(X.class);
    }

    /**
     * Class lookup should fail on assignable type.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void classLookupShouldFailOnAssignableType(){
        CallTree mt = CallTree.forClass(X.class);
        Method oI = mt.findMethod("o",new Class[]{Integer.class});
    }

    /**
     * Class lookup should fail on assignable type 2.
     */
    @Test(expected = JavaPathRuntimeException.class)
    public void classLookupShouldFailOnAssignableType2(){
        CallTree mt = CallTree.forClass(X.class);
        Method oI = mt.findMethod("o",new Class[]{String.class,Integer.class});
    }


    /**
     * Gets method.
     *
     * @param name the name
     * @param args the args
     * @return the method
     */
    Method getMethod(String name, Class<?>... args) {
        try {
            return this.getClass().getDeclaredMethod(name,args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}