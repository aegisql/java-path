package com.aegisql.java_path;

import org.junit.Test;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class MethafactoryLearning {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    String field = "FIELD";

    String str;

    public void setString(String str) {
        this.str = str;
    }

    public String getString() {
        return str;
    }


    @Test
    public void basicGetterTest() throws Throwable {
        Function<MethafactoryLearning,String> getterFunction;
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "apply",
                MethodType.methodType(Function.class), // we create Function
                MethodType.methodType(Object.class, Object.class), // Objects because Function uses generics
                lookup.findVirtual(MethafactoryLearning.class, "getString", MethodType.methodType(String.class)),//return string, get nothing
                MethodType.methodType(String.class, MethafactoryLearning.class)); //now we specify return(1) and input(2..n) types
        getterFunction = (Function<MethafactoryLearning, String>) site.getTarget().invokeExact();

        this.setString("TEST");

        String localStr = getterFunction.apply(this);

        assertEquals("TEST",localStr);


    }
/*
Assume the linkage arguments are as follows:

invokedType (describing the CallSite signature) has K parameters of types (D1..Dk) and return type Rd;
samMethodType (describing the implemented method type) has N parameters, of types (U1..Un) and return type Ru;
implMethod (the MethodHandle providing the implementation has M parameters, of types (A1..Am) and return type Ra
(if the method describes an instance method, the method type of this method handle already includes an extra first argument corresponding to the receiver);
instantiatedMethodType (allowing restrictions on invocation) has N parameters, of types (T1..Tn) and return type Rt.
Then the following linkage invariants must hold:

Rd is an interface
implMethod is a direct method handle
samMethodType and instantiatedMethodType have the same arity N, and for i=1..N, Ti and Ui are the same type, or Ti and Ui are both reference types and Ti is a subtype of Ui
Either Rt and Ru are the same type, or both are reference types and Rt is a subtype of Ru
K + N = M
For i=1..K, Di = Ai
For i=1..N, Ti is adaptable to Aj, where j=i+k
The return type Rt is void, or the return type Ra is not void and is adaptable to Rt
*/
    @Test
    public void basicSetterTest() throws Throwable {
        BiConsumer<MethafactoryLearning,String> setterConsumer;
        MethodType lambdaBodyMethodType = MethodType.methodType(void.class, String.class); //this is our consumer type - takes string, returns void
        //K=1
        MethodType invokedType = MethodType.methodType(BiConsumer.class);// we create BiConsumer, First param is setter owner, second - consuming value; return void
        //N=2
        MethodType samMethodType = MethodType.methodType(void.class,Object.class,Object.class); // Objects because Consumer uses generics; return void
        //M=2
        MethodHandle implMethod = lookup.findVirtual(MethafactoryLearning.class, "setString", lambdaBodyMethodType); //find method itself
        //N=1
        MethodType instantiatedMethodType = MethodType.methodType(void.class,MethafactoryLearning.class, String.class); //now we specify concrete return(1) and input(2..n) types

        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "accept",
                invokedType,
                samMethodType,
                implMethod,
                instantiatedMethodType);

        setterConsumer = (BiConsumer<MethafactoryLearning,String>) site.getTarget().invokeExact();

        setterConsumer.accept(this,"TEST2");

        assertEquals("TEST2",str);


    }

    @Test
    public void testFunctionWithParameter() throws Throwable {
        SimpleBean simpleBean = new SimpleBean();

        MethodType invokedType = MethodType.methodType(BiFunction.class);
        MethodType funcType = MethodType.methodType(String.class, String.class);
        MethodHandle target = lookup.findVirtual(SimpleBean.class, "simpleFunction", funcType);
        MethodType func = target.type();


        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                invokedType,
                func.generic(),
                target,
                MethodType.methodType(String.class, SimpleBean.class, String.class)
        );

        BiFunction<SimpleBean, String, String> fullFunction = (BiFunction<SimpleBean, String, String>) site.getTarget().invokeExact();


        System.out.println(fullFunction.apply(simpleBean, "FOO"));

    }

    @Test
    public void testUnreflect() throws Throwable {
        SimpleBean simpleBean = new SimpleBean();

        Method method = SimpleBean.class.getMethod("simpleFunction",String.class);

        MethodHandle target = lookup.unreflect(method);

        MethodType invokedType = MethodType.methodType(BiFunction.class);
        MethodType funcType = target.type();
        MethodType func = funcType.generic();


        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                invokedType,
                func,
                target,
                MethodType.methodType(String.class, SimpleBean.class, String.class)
        );

        BiFunction<SimpleBean, String, String> fullFunction = (BiFunction<SimpleBean, String, String>) site.getTarget().invokeExact();


        System.out.println(fullFunction.apply(simpleBean, "FOO"));

    }


    private class SimpleBean {
        public String simpleFunction(String in) {
            return "The parameter was " + in;
        }
    }

    @Test
    public void fieldGetterTest() throws Throwable{
        field = "FIELD";

        Field f = this.getClass().getDeclaredField("field");

        MethodHandle target = lookup.unreflectGetter(f);


        Function<MethafactoryLearning, String> fullFunction = obj-> {
            try {
                return (String) target.invoke(obj);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };

        assertEquals("FIELD",fullFunction.apply(this));
    }

    @Test
    public void fieldSetterTest() throws Throwable{
        field = "";
        Field f = this.getClass().getDeclaredField("field");
        MethodHandle target = lookup.unreflectSetter(f);

        BiConsumer<MethafactoryLearning, String> fullFunction = (obj,s)-> {
            try {
                target.invoke(obj,s);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
        fullFunction.accept(this,"NEWFIELD");
        assertEquals("NEWFIELD",field);
    }

}