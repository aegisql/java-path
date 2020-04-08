package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.aegisql.java_path.demo.Demo14.Field.AGE;
import static com.aegisql.java_path.demo.Demo14.Field.LAST_NAME;
import static com.aegisql.java_path.demo.Demo14.Relation.FATHER;
import static com.aegisql.java_path.demo.Demo14.Relation.MOTHER;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class Demo14 {

    enum Relation {FATHER,MOTHER}
    enum Field{FIRST_NAME,LAST_NAME,AGE}

    public static class A {
        Map<Relation,Map<Field,Object>> map;
    }

    @Test
    public void test() {
        ClassRegistry cr = new ClassRegistry();
        cr.registerClassSimpleName(Relation.class);
        cr.registerClassSimpleName(Field.class);

        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class,cr);
        pathUtils.applyValuesToPath("(HashMap map).computeIfAbsent($0,key->new HashMap).put($1,$2)", a, FATHER, Field.FIRST_NAME,"John");
        assertEquals("John",a.map.get(FATHER).get(Field.FIRST_NAME));
    }

    @Test
    public void testWithCollection() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValuesToPath("(HashMap map).computeIfAbsent($0,key->new HashMap).put($1,$2)", a, Arrays.asList(FATHER, Field.FIRST_NAME,"John"));
        assertEquals("John",a.map.get(FATHER).get(Field.FIRST_NAME));
    }

    @Test
    public void testWithLambda() {
        A a = new A();
        PathUtils pathUtils = new PathUtils(A.class);
        Function<Relation,Map<Relation,String>> lambda = k->new HashMap<>();
        pathUtils.applyValuesToPath("(HashMap map).computeIfAbsent($0,$1).put($2,$3)", a, FATHER, lambda, Field.FIRST_NAME, "John");
        pathUtils.applyValuesToPath("map.get($0).put($1,$2)", a, FATHER,  LAST_NAME, "Smith");
        pathUtils.applyValuesToPath("map.get($0).put($1,$2)", a, FATHER,  AGE, 35);
        pathUtils.applyValuesToPath("(HashMap map).computeIfAbsent($0,$1).put($2,$3)", a, MOTHER, lambda, Field.FIRST_NAME, "Ann");
        pathUtils.applyValuesToPath("map.get($0).put($1,$2)", a, MOTHER, LAST_NAME, "Smith");
        pathUtils.applyValuesToPath("map.get($0).put($1,$2)", a, MOTHER, AGE, null);
        assertEquals("John",a.map.get(FATHER).get(Field.FIRST_NAME));
        assertEquals("Smith",a.map.get(FATHER).get(LAST_NAME));
        assertEquals(35,a.map.get(FATHER).get(AGE));
        assertEquals("Ann",a.map.get(MOTHER).get(Field.FIRST_NAME));
        assertEquals("Smith",a.map.get(MOTHER).get(LAST_NAME));
        assertNull(a.map.get(MOTHER).get(AGE));
    }


}
