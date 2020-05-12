package com.aegisql.java_path.demo;

import com.aegisql.java_path.JavaPath;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The type Demo 12.
 */
public class Demo12 {

    /**
     * The type A.
     */
    public static class A {
        /**
         * The Parent.
         */
        A parent;
        /**
         * The Child.
         */
        A child;
        /**
         * The Name.
         */
        String name;

        /**
         * Instantiates a new A.
         *
         * @param parent the parent
         */
        public A(A parent) {
            this.parent = parent;
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        A a = new A(null);
        JavaPath pathUtils = new JavaPath(A.class);
        pathUtils.evalPath("child(#0).child(#1).name",a,"GRAND-CHILD");
        pathUtils.evalPath("child(#0).name",a,"CHILD");
        pathUtils.evalPath("name",a,"PARENT");
        assertNull(a.parent);
        assertNotNull(a.child);
        assertNotNull(a.child.parent);
        assertNotNull(a.child.child);
        assertNotNull(a.child.child.parent);
        assertEquals("PARENT",a.name);
        assertEquals("CHILD",a.child.name);
        assertEquals("GRAND-CHILD",a.child.child.name);
        assertEquals("CHILD",a.child.child.parent.name);
        assertEquals("PARENT",a.child.child.parent.parent.name);
    }

}
