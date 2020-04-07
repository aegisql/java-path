package com.aegisql.java_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallableNode {

    private final static Logger LOG = LoggerFactory.getLogger(CallableNode.class);

    private final Class<?> myClass;
    private Map<Class<?>, CallableNode> parameterMap = new HashMap<>();
    private final int pos;

    private Method method;
    private Field field;
    private Constructor constructor;

    public CallableNode(Class<?> myClass, int pos) {
        this.myClass = myClass;
        this.pos = pos;
    }

    public void addNode(LinkedList<Class<?>> classes, Method method) {
        if(classes.size() == 0) {
            this.method = method;
        } else {
            CallableNode callableNode = parameterMap.computeIfAbsent(classes.pollFirst(), k -> new CallableNode(k,pos+1));
            callableNode.addNode(classes,method);
        }
    }

    public void addNode(LinkedList<Class> classes, Constructor constructor) {
        if(classes.size() == 0) {
            this.constructor = constructor;
        } else {
            CallableNode callableNode = parameterMap.computeIfAbsent(classes.pollFirst(), k -> new CallableNode(k,pos+1));
            callableNode.addNode(classes,constructor);
        }
    }

    public void addNode(Field f) {
        this.field = f;
    }

    public Method findMethod(LinkedList<Class<?>> argList) {
        if(argList.size()==0) {
            return method;
        } else {
            Class<?> next = argList.pollFirst();
            if(parameterMap.containsKey(next)) {
                return parameterMap.get(next).findMethod(argList);
            } else {
                for(Class<?> cls: parameterMap.keySet()) {
                    if(cls.isAssignableFrom(next)) {
                        CallableNode callableNode = parameterMap.get(cls);
                        Method method = callableNode.findMethod(argList);
                        parameterMap.put(next, callableNode);
                        return method;
                    }
                }
                throw new JavaPathRuntimeException("findMethod failed to find assignable class "+next+" in "+parameterMap.keySet());
            }
        }
    }

    public Constructor findConstructor(LinkedList<Class<?>> argList) {
        if(argList.size()==0) {
            return constructor;
        } else {
            Class<?> next = argList.pollFirst();
            if(parameterMap.containsKey(next)) {
                return parameterMap.get(next).findConstructor(argList);
            } else {
                for(Class<?> cls: parameterMap.keySet()) {
                    if(cls.isAssignableFrom(next)) {
                        CallableNode callableNode = parameterMap.get(cls);
                        Constructor constructor = callableNode.findConstructor(argList);
                        parameterMap.put(next, callableNode);
                        return constructor;
                    }
                }
                throw new JavaPathRuntimeException("findConstructor failed to find assignable class "+next+" in "+parameterMap.keySet());
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallableNode{");
        sb.append("pos=").append(pos);
        sb.append(", class=").append(myClass);
        sb.append(", parameters=").append(parameterMap);
        if(method != null) sb.append(", method=").append(method);
        if(field != null) sb.append(", field=").append(field);
        if(constructor != null) sb.append(", constructor=").append(constructor);
        sb.append('}');
        return sb.toString();
    }

    public Method getMethod() {
        return method;
    }

    public Field getField() {
        return field;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public void findMethodCandidates(LinkedList<Class<?>> argList, List<Method> candidates) {
        if(argList.size()==0) {
            candidates.add(method);
        } else {
            Class<?> next = argList.pollFirst();
            if(next == null) {
                List<Method> collect = parameterMap.values().stream().map(CallableNode::getMethod).collect(Collectors.toList());
                candidates.addAll(collect);
            } else {
                CallableNode callableNode = parameterMap.get(next);
                if(callableNode != null) {
                    callableNode.findMethodCandidates(argList, candidates);
                } else {
                    for(Class<?> cls: parameterMap.keySet()) {
                        if(cls.isAssignableFrom(next)) {
                            callableNode = parameterMap.get(cls);
                            parameterMap.put(next, callableNode);
                            callableNode.findMethodCandidates(argList, candidates);
                            return;
                        }
                    }
                    throw new JavaPathRuntimeException("findMethodCandidates failed to find assignable class "+next+" in "+parameterMap.keySet());
                }
            }
        }
    }

    public void findConstructorCandidates(LinkedList<Class<?>> argList, List<Constructor> candidates) {
        if(argList.size()==0) {
            candidates.add(constructor);
        } else {
            Class<?> next = argList.pollFirst();
            if(next == null) {
                List<Constructor> collect = parameterMap.values().stream().map(CallableNode::getConstructor).collect(Collectors.toList());
                candidates.addAll(collect);
            } else {
                CallableNode callableNode = parameterMap.get(next);
                if(callableNode != null) {
                    callableNode.findConstructorCandidates(argList, candidates);
                } else {
                    for(Class<?> cls: parameterMap.keySet()) {
                        if(cls.isAssignableFrom(next)) {
                            callableNode = parameterMap.get(cls);
                            parameterMap.put(next, callableNode);
                            callableNode.findConstructorCandidates(argList, candidates);
                            return;
                        }
                    }
                    throw new JavaPathRuntimeException("findConstructorCandidates failed to find assignable class "+next+" in "+parameterMap.keySet());
                }
            }
        }
    }

}
