package com.aegisql.java_path;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The type Parametrized property test.
 */
public class ParametrizedPropertyTest {

    /**
     * The Class registry.
     */
    ClassRegistry classRegistry = new ClassRegistry();

    private ParametrizedProperty p(String str) {
        List<TypedPathElement> parse = JavaPathParser.parse("x("+str+")");
        assertTrue(parse.size() == 1);
        return new ParametrizedProperty(classRegistry,parse.get(0).getParameters().get(0),false);
    }

    /**
     * Basic test.
     */
    @Test
    public void basicTest() {
        ParametrizedProperty lp1 = p("a");
        assertEquals("a",lp1.getPropertyStr());
        assertEquals("a",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }


    /**
     * Basic quoted test.
     */
    @Test
    public void basicQuotedTest() {
        ParametrizedProperty lp1 = p("'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Basic test with quote.
     */
    @Test
    public void basicTestWithQuote() {
        ParametrizedProperty lp1 = p("'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Basic test with str type.
     */
    @Test
    public void basicTestWithStrType() {
        ParametrizedProperty lp1 = p("str 'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Basic test with str type and quotes.
     */
    @Test
    public void basicTestWithStrTypeAndQuotes() {
        ParametrizedProperty lp1 = p("str 'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Basic test with int type.
     */
    @Test
    public void basicTestWithIntType() {
        ParametrizedProperty lp1 = p("i 0");
        assertEquals("0",lp1.getPropertyStr());
        assertEquals(0,lp1.getProperty());
        assertEquals(int.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Quotes work test.
     */
    @Test
    public void quotesWorkTest() {
        ParametrizedProperty lp1 = p("'int 0'");
        assertEquals("int 0",lp1.getPropertyStr());
        assertEquals("int 0",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * The enum X.
     */
    enum X {
        /**
         * A x.
         */
        A,
        /**
         * B x.
         */
        B}

    /**
     * Basic test with enum type.
     */
    @Test
    public void basicTestWithEnumType() {
        ParametrizedProperty lp1 = p(X.class.getName()+" A");
        assertEquals("A",lp1.getPropertyStr());
        assertEquals(X.A,lp1.getProperty());
        assertEquals(X.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Builder test.
     */
    @Test
    public void builderTest() {
        ParametrizedProperty lp1 = p("#");
        assertEquals("#",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(null,lp1.getPropertyType());
        assertTrue(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    /**
     * Value test.
     */
    @Test
    public void valueTest() {
        ParametrizedProperty lp1 = p("$");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(null,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }

    /**
     * Value with type test.
     */
    @Test
    public void valueWithTypeTest() {
        ParametrizedProperty lp1 = p("int $");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(int.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }

    /**
     * Value with full type test.
     */
    @Test
    public void valueWithFullTypeTest() {
        ParametrizedProperty lp1 = p("java.lang.Integer $");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(Integer.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }



}