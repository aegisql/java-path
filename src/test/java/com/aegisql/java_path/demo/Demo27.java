package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * The type Demo 1.
 */
public class Demo27 {

    /**
     * Test.
     */
    @Test
    public void testPrivateField() {
        String str = "VALUE";
        JavaPath javaPath = new JavaPath(String.class);
        assertEquals("VALUE",str);
        javaPath.evalPath("value",str,"THE HACK".getBytes());
        assertEquals("THE HACK",str);
    }

    @Test
    public void testPrivateFieldJoke() {
        Integer val100 = new Integer(100);
        JavaPath javaPath = new JavaPath(Integer.class);
        assertEquals(Integer.valueOf(100),val100);
        javaPath.evalPath("value(int 1000)",val100);
        assertEquals(Integer.valueOf(1000),val100);
    }

}
