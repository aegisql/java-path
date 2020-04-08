package com.aegisql.java_path.demo;

import com.aegisql.java_path.PathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class Demo12 {

    public static class A {
        A parent;
        A child;
        String name;
        public A(A parent) {
            this.parent = parent;
        }
    }

    @Test
    public void test() {
        A a = new A(null);
        PathUtils pathUtils = new PathUtils(A.class);
        pathUtils.applyValueToPath("name",a,"PARENT");
        pathUtils.applyValueToPath("child(#0).name",a,"CHILD");
        pathUtils.applyValueToPath("child(#0).child(#1).name",a,"GRAND-CHILD");
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
