package com.aegisql.java_path;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static junit.framework.TestCase.*;

/**
 * The type Tu.
 */
public class TU {

    private List<TypedValue> typedValues;
    private List<TypedPathElement> pathElements;
    private List<Runnable> consumers = new ArrayList<>();
    private int nextElementConsumer = 0;
    private int nextValueConsumer = 0;


    /**
     * For path elements tu.
     *
     * @param i            the
     * @param pathElements the path elements
     * @return the tu
     */
    public static TU forPathElements(int i, List<TypedPathElement> pathElements) {
        TU tu = new TU();
        assertNotNull("pathElements list is null",pathElements);
        assertEquals("Expected "+i+" pathElements but was "+pathElements.size() + " in path "+pathElements,i,pathElements.size());
        tu.pathElements = pathElements;
        return tu;
    }

    /**
     * For path elements tu.
     *
     * @param i          the
     * @param typedValue the typed value
     * @return the tu
     */
    public static TU forPathElements(int i,TypedValue typedValue) {
        TU tu = new TU();
        assertNotNull("typedValue is null",typedValue);
        return forPathElements(i,typedValue.getTypedPathElements());
    }

    /**
     * For typed values tu.
     *
     * @param i                the
     * @param typedPathElement the typed path element
     * @return the tu
     */
    public static TU forTypedValues(int i, TypedPathElement typedPathElement) {
        return TU.forTypedValues(i,typedPathElement.getParameters());
    }

    /**
     * For typed values tu.
     *
     * @param i           the
     * @param typedValues the typed values
     * @return the tu
     */
    public static TU forTypedValues(int i, List<TypedValue> typedValues) {
        TU tu = new TU();
        assertNotNull("typedValue list is null",typedValues);
        assertEquals("Expected "+i+" typedValues but was "+typedValues.size() + " for values "+typedValues,i,typedValues.size());
        tu.typedValues = typedValues;
        return tu;
    }

    /**
     * Accept path element tu.
     *
     * @param pathElementConsumer the path element consumer
     * @return the tu
     */
    public TU acceptPathElement(Consumer<TypedPathElement> pathElementConsumer) {
        acceptElement(nextElementConsumer++,pathElementConsumer);
        return this;
    }

    /**
     * Accept typed value tu.
     *
     * @param pathElementConsumer the path element consumer
     * @return the tu
     */
    public TU acceptTypedValue(Consumer<TypedValue> pathElementConsumer) {
        acceptValue(nextValueConsumer++,pathElementConsumer);
        return this;
    }

    private TU acceptElement(int i, Consumer<TypedPathElement> pathElementConsumer) {
        consumers.add(()->pathElementConsumer.accept(pathElements.get(i)));
        return this;
    }

    private TU acceptValue(int i, Consumer<TypedValue> typedValueConsumer) {
        consumers.add(()->typedValueConsumer.accept(typedValues.get(i)));
        return this;
    }

    /**
     * Test.
     */
    public void test() {
        consumers.forEach(Runnable::run);
    }

    /**
     * Assert type.
     *
     * @param cls        the cls
     * @param typedValue the typed value
     */
    public static void assertType(String cls, TypedValue typedValue) {
        assertNotNull("typedValue is null",typedValue);
        if(typedValue.getType() != null) {
            if(cls == null) {
                assertNull("Not null class for typedValue "+typedValue,typedValue.getType());
            } else {
                assertEquals("Not matching class " + cls + " for typedValue " + typedValue, cls, typedValue.getType());
            }
        } else if(cls != null) {
            fail("Expected class "+cls+" for typedValue "+typedValue);
        }
    }

    /**
     * Assert type.
     *
     * @param cls         the cls
     * @param pathElement the path element
     */
    public static void assertType(String cls, TypedPathElement pathElement) {
        assertNotNull("pathElement is null",pathElement);
        if(pathElement.getType() != null) {
            if(cls == null) {
                assertNull("Not null class for pathElement "+pathElement,pathElement.getType());
            } else {
                assertEquals("Not matching class " + cls + " for pathElement " + pathElement, cls, pathElement.getType());
            }
        } else if(cls != null) {
            fail("Expected class "+cls+" for pathElement "+pathElement);
        }
    }

    /**
     * Assert name.
     *
     * @param name        the name
     * @param pathElement the path element
     */
    public static void assertName(String name, TypedPathElement pathElement) {
        assertNotNull("pathElement is null",pathElement);
        assertNotNull("pathElement name is null",pathElement.getName());
        assertEquals(name,pathElement.getName());
    }

    /**
     * Assert factory name.
     *
     * @param name        the name
     * @param pathElement the path element
     */
    public static void assertFactoryName(String name, TypedPathElement pathElement) {
        assertNotNull("pathElement is null",pathElement);
        assertNotNull("pathElement name is null",pathElement.getName());
        assertEquals(name,pathElement.getFactory());
    }

    /**
     * Assert factory name.
     *
     * @param name       the name
     * @param typedValue the typed value
     */
    public static void assertFactoryName(String name, TypedValue typedValue) {
        assertNotNull("typedValue is null",typedValue);
        assertEquals(name,typedValue.getFactory());
    }

    /**
     * Assert value equals.
     *
     * @param val        the val
     * @param typedValue the typed value
     */
    public static void assertValueEquals(Object val, TypedValue typedValue) {
        assertNotNull("typedValue is null",typedValue);
        if(typedValue.getValue() != null) {
            assertEquals("Not equal value "+val+" for typedValue "+typedValue,val,typedValue.getValue());
        } else if(val != null) {
            fail("Expected value "+val+" for typedValue "+typedValue);
        }
    }

    /**
     * Assert value reference.
     *
     * @param i          the
     * @param typedValue the typed value
     */
    public static void assertValueReference(int i, TypedValue typedValue) {
        assertNotNull("typedValue is null",typedValue);
        assertTrue(typedValue.isDollarSign());
        assertEquals(i,typedValue.getValueIdx());
    }

    /**
     * Assert back reference.
     *
     * @param i          the
     * @param typedValue the typed value
     */
    public static void assertBackReference(int i, TypedValue typedValue) {
        assertNotNull("typedValue is null",typedValue);
        assertTrue(typedValue.isHashSign());
        assertEquals(i,typedValue.getBackRefIdx());
    }

}
