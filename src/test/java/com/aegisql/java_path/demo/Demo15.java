package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.aegisql.java_path.demo.Demo15.Field.*;
import static com.aegisql.java_path.demo.Demo15.Relation.FATHER;
import static com.aegisql.java_path.demo.Demo15.Relation.MOTHER;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The type Demo 15.
 */
public class Demo15 {

    /**
     * The enum Relation.
     */
    enum Relation {
        /**
         * Father relation.
         */
        FATHER,
        /**
         * Mother relation.
         */
        MOTHER}

    /**
     * The enum Field.
     */
    enum Field{
        /**
         * First name field.
         */
        FIRST_NAME,
        /**
         * Last name field.
         */
        LAST_NAME,
        /**
         * Age field.
         */
        AGE}

    /**
     * Test.
     */
    @Test
    public void test() {
        JavaPath pathUtils = new JavaPath(HashMap.class);
        Function<Relation,Map<Relation,String>> lambda = k->new HashMap<>();
        HashMap<Relation,Map<Field,Object>> map = pathUtils.initPath(HashMap.class,"#.computeIfAbsent($0,$1).put($2,$3)",  FATHER, lambda, FIRST_NAME, "John");
        assertNotNull(map);
        pathUtils.evalPath("#.get($0).put($1,$2)", map, FATHER,  LAST_NAME, "Smith");
        pathUtils.evalPath("#.get($0).put($1,$2)", map, FATHER,  AGE, 35);
        pathUtils.evalPath("#.computeIfAbsent($0,$1).put($2,$3)", map, MOTHER, lambda, Field.FIRST_NAME, "Ann");
        pathUtils.evalPath("#.get($0).put($1,$2)", map, MOTHER, LAST_NAME, "Smith");
        pathUtils.evalPath("#.get($0).put($1,$2)", map, MOTHER, AGE, null);
        assertEquals("John",map.get(FATHER).get(Field.FIRST_NAME));
        assertEquals("Smith",map.get(FATHER).get(LAST_NAME));
        assertEquals(35,map.get(FATHER).get(AGE));
        assertEquals("Ann",map.get(MOTHER).get(Field.FIRST_NAME));
        assertEquals("Smith",map.get(MOTHER).get(LAST_NAME));
        assertNull(map.get(MOTHER).get(AGE));
    }


}
