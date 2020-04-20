package com.aegisql.java_path.demo;

import com.aegisql.java_path.ClassRegistry;
import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.aegisql.java_path.demo.Demo11.PhoneType.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class Demo11 {

    enum PhoneType{HOME,CELL,WORK}

    public static class A {
        String firstName;
        String lastName;
        Map<PhoneType, Set<String>> phones;
        Map<String, PhoneType> reversedPhones;
    }

    @Test
    public void test() {
        A a = new A();

        ClassRegistry  classRegistry = new ClassRegistry();
        classRegistry.registerClassSimpleName(PhoneType.class);

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

        pathUtils.evalPath("phones.get(PhoneType CELL).add", a, "1-101-111-2233");
        pathUtils.evalPath("phones.get(PhoneType HOME).add", a, "1-101-111-7865");
        pathUtils.evalPath("phones.get(PhoneType WORK).add", a, "1-105-333-1100");
        // Dollar sign is not required if it is the last or the only parameter of the method
        pathUtils.evalPath("phones.get(PhoneType WORK).add($)", a, "1-105-333-1104");

        // Dollar sign is required because it is not the last of two required parameters
        pathUtils.evalPath("reversedPhones.put($,PhoneType CELL)", a, "1-101-111-2233");
        pathUtils.evalPath("reversedPhones.put($,PhoneType HOME)", a, "1-101-111-7865");
        pathUtils.evalPath("reversedPhones.put($,PhoneType WORK)", a, "1-105-333-1100");
        pathUtils.evalPath("reversedPhones.put($,PhoneType WORK)", a, "1-105-333-1104");

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

}
