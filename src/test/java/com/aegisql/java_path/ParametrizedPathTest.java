package com.aegisql.java_path;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParametrizedPathTest {

    String s1 = "test"; // non parametrized
    String s2 = "test{a}"; //parametrized with String 'a'
    String s3 = "test{str 'str a', int 0}"; // parametrized with String 'str a' and Integer(0)
    String s4 = "test{#, $, str a, int 0}"; // parametrized with builder, value with auto class, String 'a' and Integer(0)
    String s5 = "test{#, $java.lang.Integer, str a, int 0}"; // parametrized with builder, value with explicit class Integer, String 'a' and Integer(0)

    ClassRegistry classRegistry = new ClassRegistry();

    private ParametrizedPath p(Class<?> aClass, String str) {
        List<TypedPathElement> parse = JavaPathParser.parse(str);
        assertTrue(parse.size() == 1);
        return new ParametrizedPath(classRegistry,aClass,parse.get(0));
    }

    @Test
    public void basicGetterTest() {
        ParametrizedPath pl1 = p(String.class,"test");
        assertEquals(s1,pl1.getLabel());
        assertEquals(0,pl1.getClassesForGetter(new ReferenceList("builder"),"value".getClass()).length);
        assertEquals(0,pl1.getPropertiesForGetter(new ReferenceList("builder"),"value").length);

        ParametrizedPath pl2 = p(String.class,s2);
        assertEquals("test",pl1.getLabel());
        assertEquals("a",pl2.getPropertiesForGetter(new ReferenceList("builder"),"value")[0]);
        assertEquals(1,pl2.getClassesForGetter(new ReferenceList("builder"),"value".getClass()).length);
        assertEquals(String.class,pl2.getClassesForGetter(new ReferenceList("builder"),Integer.valueOf(1).getClass())[0]);
    }

    @Test
    public void basicSetterTest() {
        ParametrizedPath pl1 = p(String.class,"test");
        assertEquals(s1,pl1.getLabel());
        assertEquals(1,pl1.getClassesForSetter(new ReferenceList("builder"),"value".getClass()).length);
        assertEquals(1,pl1.getPropertiesForSetter(new ReferenceList("builder"),"value").length);

        ParametrizedPath pl2 = p(String.class,s2);
        assertEquals("test",pl1.getLabel());
        assertEquals("a",pl2.getPropertiesForSetter(new ReferenceList("builder"),"value")[0]);
        assertEquals("value",pl2.getPropertiesForSetter(new ReferenceList("builder"),"value")[1]);
        assertEquals(2,pl2.getClassesForSetter(new ReferenceList("builder"),"value".getClass()).length);
        assertEquals(String.class,pl2.getClassesForSetter(new ReferenceList("builder"),Integer.valueOf(1).getClass())[0]);
        assertEquals(Integer.class,pl2.getClassesForSetter(new ReferenceList("builder"),Integer.valueOf(1).getClass())[1]);
    }


}