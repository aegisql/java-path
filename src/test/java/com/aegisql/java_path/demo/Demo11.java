package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.aegisql.java_path.demo.Demo11.PhoneType.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * The type Demo 11.
 */
public class Demo11 {

    /**
     * The enum Phone type.
     */
    enum PhoneType{
        /**
         * Home phone type.
         */
        HOME,
        /**
         * Cell phone type.
         */
        CELL,
        /**
         * Work phone type.
         */
        WORK}

    /**
     * The type A.
     */
    public static class A {
        /**
         * The First name.
         */
        public String firstName;
        /**
         * The Last name.
         */
        public String lastName;
        /**
         * The Phones.
         */
        public Map<PhoneType, Set<String>> phones;
        /**
         * The Reversed phones.
         */
        public Map<String, PhoneType> reversedPhones;
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A();

        ClassRegistry  classRegistry = new ClassRegistry();
        classRegistry.registerClass(PhoneType.class,PhoneType.class.getSimpleName());

        JavaPath pathUtils = new JavaPath(A.class,classRegistry);

        //init objects
        //pass value explicitly
        pathUtils.evalPath("(map phones).put(PhoneType WORK)", a, new HashSet<>());
        //or let the Map method do the job
        pathUtils.evalPath("phones.computeIfAbsent(PhoneType HOME,key->new HashSet).@", a);
        pathUtils.evalPath("phones.computeIfAbsent(PhoneType CELL,key->new HashSet).@", a);
        pathUtils.evalPath("(map reversedPhones).@", a);

        pathUtils.evalPath("firstName", a, "John");
        pathUtils.evalPath("lastName", a, "Smith");

        pathUtils.evalPath("phones.get(PhoneType CELL).add($0); reversedPhones.put($0,PhoneType CELL)", a, "1-101-111-2233");
        pathUtils.evalPath("phones.get(PhoneType HOME).add($0); reversedPhones.put($0,PhoneType HOME)", a, "1-101-111-7865");
        pathUtils.evalPath("phones.get(PhoneType WORK).add($0); reversedPhones.put($0,PhoneType WORK)", a, "1-105-333-1100");
        // Dollar sign is not required if it is the last or the only parameter of the method
        pathUtils.evalPath("phones.get(PhoneType WORK).add; reversedPhones.put($0,PhoneType WORK)", a, "1-105-333-1104");

        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(WORK,a.reversedPhones.get("1-105-333-1100"));
        assertEquals(WORK,a.reversedPhones.get("1-105-333-1104"));
        assertEquals(CELL,a.reversedPhones.get("1-101-111-2233"));
        assertEquals(HOME,a.reversedPhones.get("1-101-111-7865"));

        assertTrue(a.phones.get(HOME).contains("1-101-111-7865"));
        assertTrue(a.phones.get(CELL).contains("1-101-111-2233"));
        assertTrue(a.phones.get(WORK).contains("1-105-333-1100"));
        assertTrue(a.phones.get(WORK).contains("1-105-333-1104"));
    }

    @Test
    public void setPathAliasTest() {
        A a = new A();

        ClassRegistry  classRegistry = new ClassRegistry();
        classRegistry.registerClass(PhoneType.class,PhoneType.class.getSimpleName());

        JavaPath pathUtils = new JavaPath(A.class,classRegistry);

        pathUtils.setPathAlias("phones.get(PhoneType CELL).add($0); reversedPhones.put($0,PhoneType CELL)","setCellPhone");
        pathUtils.setPathAlias("phones.get(PhoneType HOME).add($0); reversedPhones.put($0,PhoneType HOME)","setHomePhone");
        pathUtils.setPathAlias("phones.get(PhoneType WORK).add($0); reversedPhones.put($0,PhoneType WORK)","setWorkPhone");

        //init objects
        //pass value explicitly
        pathUtils.evalPath("(map phones).put(PhoneType WORK)", a, new HashSet<>());
        //or let the Map method do the job
        pathUtils.evalPath("phones.computeIfAbsent(PhoneType HOME,key->new HashSet).@", a);
        pathUtils.evalPath("phones.computeIfAbsent(PhoneType CELL,key->new HashSet).@", a);
        pathUtils.evalPath("(map reversedPhones).@", a);

        pathUtils.evalPath("firstName", a, "John");
        pathUtils.evalPath("lastName", a, "Smith");

        pathUtils.evalPath("setCellPhone", a, "1-101-111-2233");
        pathUtils.evalPath("setHomePhone", a, "1-101-111-7865");
        pathUtils.evalPath("setWorkPhone", a, "1-105-333-1100");
        // Dollar sign is not required if it is the last or the only parameter of the method
        pathUtils.evalPath("setWorkPhone", a, "1-105-333-1104");

        assertEquals("John",a.firstName);
        assertEquals("Smith",a.lastName);
        assertEquals(WORK,a.reversedPhones.get("1-105-333-1100"));
        assertEquals(WORK,a.reversedPhones.get("1-105-333-1104"));
        assertEquals(CELL,a.reversedPhones.get("1-101-111-2233"));
        assertEquals(HOME,a.reversedPhones.get("1-101-111-7865"));

        assertTrue(a.phones.get(HOME).contains("1-101-111-7865"));
        assertTrue(a.phones.get(CELL).contains("1-101-111-2233"));
        assertTrue(a.phones.get(WORK).contains("1-105-333-1100"));
        assertTrue(a.phones.get(WORK).contains("1-105-333-1104"));

    }

    @Test
    public void perfNoCachingTest() {
        A a = new A();

        ClassRegistry  classRegistry = new ClassRegistry();
        classRegistry.registerClass(PhoneType.class,PhoneType.class.getSimpleName());
        classRegistry.registerStringConverter(PhoneType.class,PhoneType::valueOf);

        JavaPath pathUtils = new JavaPath(A.class,classRegistry);
        pathUtils.setEnablePathCaching(false);
        long start = System.nanoTime();
        evalLoop("no cache",pathUtils);
        long end = System.nanoTime();
        System.out.println("Total time: "+(end-start)/1.0E9);
    }

    @Test
    public void perfWithCachingTest() {
        A a = new A();

        ClassRegistry  classRegistry = new ClassRegistry();
        classRegistry.registerClass(PhoneType.class,PhoneType.class.getSimpleName());

        JavaPath pathUtils = new JavaPath(A.class,classRegistry);
        pathUtils.setEnablePathCaching(true);

        long start = System.nanoTime();
        evalLoop("cache",pathUtils);
        long end = System.nanoTime();
        System.out.println("Total time: "+(end-start)/1.0E9);
    }

    public static int LOOP_SIZE = 1000001;

    private void evalLoop(String name, JavaPath pathUtils) {
        A a;
        long start = System.nanoTime();

        for(int i = 1; i < LOOP_SIZE; i++) {
            a = new A();
            if(i % 10000 == 0) {
                long end = System.nanoTime();
                System.out.println(name+" "+i + " "+(end-start)/1.0E9);
                start = end;
            }
            pathUtils.evalPath("(HashMap phones).computeIfAbsent(PhoneType WORK,key->new HashSet).@", a);
            pathUtils.evalPath("phones.computeIfAbsent(PhoneType HOME,key->new HashSet).@", a);
            pathUtils.evalPath("phones.computeIfAbsent(PhoneType CELL,key->new HashSet).@", a);
            pathUtils.evalPath("(map reversedPhones).@", a);
            pathUtils.evalPath("firstName", a, "John");
            pathUtils.evalPath("lastName", a, "Smith");
            pathUtils.evalPath("phones.get(PhoneType CELL).add($0); reversedPhones.put($0,PhoneType CELL)", a, "1-101-111-2233");
            pathUtils.evalPath("phones.get(PhoneType HOME).add($0); reversedPhones.put($0,PhoneType HOME)", a, "1-101-111-7865");
            pathUtils.evalPath("phones.get(PhoneType WORK).add($0); reversedPhones.put($0,PhoneType WORK)", a, "1-105-333-1100");
            pathUtils.evalPath("phones.get(PhoneType WORK).add; reversedPhones.put($0,PhoneType WORK)", a, "1-105-333-1104");
        }
    }

    @Test
    public void evalDirectLoop() {
        A a;
        long start = System.nanoTime();
        for(int i = 1; i < LOOP_SIZE; i++) {
            a = new A();
            //init objects
            if(i % 10000 == 0) {
                System.out.println("direct "+i);
            }
            a.phones = new HashMap<>();
            a.phones.put(WORK,new HashSet<>());
            a.phones.put(HOME,new HashSet<>());
            a.phones.put(CELL,new HashSet<>());
            a.reversedPhones = new HashMap<>();
            a.firstName = "John";
            a.lastName = "Smith";
            a.phones.get(CELL).add("1-101-111-2233");
            a.reversedPhones.put("1-101-111-2233",CELL);
            a.phones.get(HOME).add("1-101-111-7865");
            a.reversedPhones.put("1-101-111-7865",HOME);
            a.phones.get(WORK).add("1-105-333-1100");
            a.reversedPhones.put("1-105-333-1100",WORK);
            a.phones.get(WORK).add("1-105-333-1104");
            a.reversedPhones.put("1-105-333-1104",WORK);
        }
        long end = System.nanoTime();
        System.out.println("Total time: "+(end-start)/1.0E9);
    }


}
