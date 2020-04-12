package com.aegisql.java_path;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParametrizedPathTest {

    String s1 = "test"; // non parametrized
    String s2 = "test(a)"; //parametrized with String 'a'

    ClassRegistry classRegistry = new ClassRegistry();

    private ParametrizedPath p(String str) {
        List<TypedPathElement> parse = JavaPathParser.parse(str);
        assertTrue(parse.size() == 1);
        return new ParametrizedPath(classRegistry,parse.get(0));
    }

    @Test
    public void basicGetterTest() {
        ParametrizedPath pl1 = p("test");
        assertEquals(s1,pl1.getLabel());
        assertEquals(0,pl1.getClassesForGetter(new ReferenceList("builder","value")).length);
        assertEquals(0,pl1.getPropertiesForGetter(new ReferenceList("builder","value")).length);

        ParametrizedPath pl2 = p(s2);
        assertEquals("test",pl1.getLabel());
        assertEquals("a",pl2.getPropertiesForGetter(new ReferenceList("builder","value"))[0]);
        assertEquals(1,pl2.getClassesForGetter(new ReferenceList("builder","value")).length);
        assertEquals(String.class,pl2.getClassesForGetter(new ReferenceList("builder",Integer.valueOf(1)))[0]);
    }

    @Test
    public void basicSetterTest() {
        ParametrizedPath pl1 = p("test");
        assertEquals(s1,pl1.getLabel());
        assertEquals(1,pl1.getClassesForSetter(new ReferenceList("builder","value")).length);
        assertEquals(1,pl1.getPropertiesForSetter(new ReferenceList("builder","value")).length);

        ParametrizedPath pl2 = p(s2);
        assertEquals("test",pl1.getLabel());
        assertEquals("a",pl2.getPropertiesForSetter(new ReferenceList("builder","value"))[0]);
        assertEquals("value",pl2.getPropertiesForSetter(new ReferenceList("builder","value"))[1]);
        assertEquals(2,pl2.getClassesForSetter(new ReferenceList("builder","value")).length);
        assertEquals(String.class,pl2.getClassesForSetter(new ReferenceList("builder",Integer.valueOf(1)))[0]);
        assertEquals(Integer.class,pl2.getClassesForSetter(new ReferenceList("builder",Integer.valueOf(1)))[1]);
    }


}