package com.aegisql.java_path;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParametrizedPropertyTest {

    @Test
    public void basicTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("a b");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void basicTestWithQuote() {
        ParametrizedProperty lp1 = new ParametrizedProperty("'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void basicTestWithStrType() {
        ParametrizedProperty lp1 = new ParametrizedProperty("str a b");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void basicTestWithStrTypeAndQuotes() {
        ParametrizedProperty lp1 = new ParametrizedProperty("str 'a b'");
        assertEquals("a b",lp1.getPropertyStr());
        assertEquals("a b",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void basicTestWithIntType() {
        ParametrizedProperty lp1 = new ParametrizedProperty("i 0");
        assertEquals("0",lp1.getPropertyStr());
        assertEquals(0,lp1.getProperty());
        assertEquals(int.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void quotesWorkTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("'int 0'");
        assertEquals("int 0",lp1.getPropertyStr());
        assertEquals("int 0",lp1.getProperty());
        assertEquals(String.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    enum X {A,B}

    @Test
    public void basicTestWithEnumType() {
        ParametrizedProperty lp1 = new ParametrizedProperty(X.class.getName()+" A");
        assertEquals("A",lp1.getPropertyStr());
        assertEquals(X.A,lp1.getProperty());
        assertEquals(X.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void builderTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("#");
        assertEquals("#",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(null,lp1.getPropertyType());
        assertTrue(lp1.isBuilder());
        assertFalse(lp1.isValue());
    }

    @Test
    public void valueTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("$");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(null,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }

    @Test
    public void valueWithTypeTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("$int");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(int.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }

    @Test
    public void valueWithFullTypeTest() {
        ParametrizedProperty lp1 = new ParametrizedProperty("$java.lang.Integer");
        assertEquals("$",lp1.getPropertyStr());
        assertEquals(null,lp1.getProperty());
        assertEquals(Integer.class,lp1.getPropertyType());
        assertFalse(lp1.isBuilder());
        assertTrue(lp1.isValue());
    }



}