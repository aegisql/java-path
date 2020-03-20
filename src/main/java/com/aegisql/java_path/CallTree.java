package com.aegisql.java_path;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CallTree {

    private final Map<String,Map<Class<?>, CallableNode>> namesMap = new HashMap<>();

    private final Set<String> knownLabels = new HashSet<>();

    public CallTree() {
    }

    public CallTree(Object instance) {
        this(Objects.requireNonNull(instance,"Failed to build MethodTree. Instance is null").getClass());
    }

    public CallTree(Class<?> c) {
        Arrays.stream(c.getDeclaredFields()).forEach(this::addField);
        Arrays.stream(c.getDeclaredMethods()).forEach(this::addMethod);
        Class sClass = c.getSuperclass();
        if(sClass != null) {
            CallTree inner = new CallTree(sClass);
            inner.knownLabels.forEach(l->{
                if(knownLabels.contains(l)) {
                    throw new JavaPathRuntimeException("Found duplicate label "+l+" in "+c.getSimpleName()+" conflicting with "+sClass.getSimpleName());
                } else {
                    knownLabels.add(l);
                }
            });
            namesMap.putAll(inner.namesMap);
        }
    }

    private void addField(Field f) {

        String name = f.getName();

        NoLabel noLabel = f.getAnnotation(NoLabel.class);
        Label label     = f.getAnnotation(Label.class);
        if(noLabel != null) {
            if(label == null) {
                return;
            } else {
                throw new JavaPathRuntimeException("Field " + f + " has both @Label and @NoLabel annotations. Please remove one.");
            }
        }
        if(label != null) {
            Arrays.stream(label.value()).forEach(l->{
                if(knownLabels.contains(l)) {
                    throw new JavaPathRuntimeException("Duplicated label " + l + " found for field "+f);
                }
                knownLabels.add(l);
            });
        }

        Map<Class<?>, CallableNode> parameterMap = namesMap.computeIfAbsent(name, n -> new HashMap<>());
        CallableNode callableNode = parameterMap.computeIfAbsent(f.getType(), p -> new CallableNode(p,0));
        callableNode.addNode(f,0);
    }

    public void addMethod(Method method) {
        String name = method.getName();
        NoLabel noLabel = method.getAnnotation(NoLabel.class);
        Label label     = method.getAnnotation(Label.class);

        if(noLabel != null) {
            if(label == null) {
                return;
            } else {
                throw new JavaPathRuntimeException("Method " + method + " has both @Label and @NoLabel annotations. Please remove one.");
            }
        }

        Map<Class<?>, CallableNode> parameterMap = namesMap.computeIfAbsent(name, n -> new HashMap<>());
        LinkedList<Class<?>> args = new LinkedList<>();
        args.addAll(Arrays.asList(method.getParameterTypes()));
        CallableNode callableNode;
        if(method.getParameterCount() == 0) {
            callableNode = parameterMap.computeIfAbsent(null, p -> new CallableNode(p,0));
            callableNode.addNode(new LinkedList<>(),method);
        } else {
            Class<?> pClass = args.pollFirst();
            callableNode = parameterMap.computeIfAbsent(pClass, p -> new CallableNode(p,0));
            callableNode.addNode(args,method);
        }
        if(label != null) {
            Arrays.stream(label.value())
                    .filter(l-> ! l.equals(name))
                    .peek(l->{
                        if(knownLabels.contains(l) || (namesMap.containsKey(l) && parameterMap != namesMap.get(l))) {
                            throw new JavaPathRuntimeException("Method "+method+" labeled as '"+l+"' already has entry with the same name: "+namesMap.get(l));
                        }
                        knownLabels.add(l);
                    })
                    .forEach(l->namesMap.put(l,parameterMap));
        }
    }

    public Method findMethod(String name, Class<?> ... args) {
        LinkedList<Class<?>> argList = new LinkedList<>();
        if(args != null) {
            argList.addAll(Arrays.asList(args));
        }
        if(namesMap.containsKey(name)) {
            Map<Class<?>, CallableNode> parameterMap = namesMap.get(name);
            if(argList.size() == 0 && parameterMap.containsKey(null)) {
                return parameterMap.get(null).getMethod();
            } else {
                Class<?> first = argList.pollFirst();
                if(parameterMap.containsKey(first)) {
                    CallableNode callableNode = parameterMap.get(first);
                    return callableNode.findMethod(argList);
                } else {
                    for(Class<?> cls: parameterMap.keySet()) {
                        if(cls.isAssignableFrom(first)) {
                            CallableNode callableNode = parameterMap.get(cls);
                            Method method = callableNode.findMethod(argList);
                            parameterMap.put(first, callableNode);
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Set<Method> findMethodCandidates(String name, Class<?> ... args) {
        List<Method> candidates = new LinkedList<>();
        LinkedList<Class<?>> argList = new LinkedList<>();
        if(args != null) {
            argList.addAll(Arrays.asList(args));
        }
        if(namesMap.containsKey(name)) {
            Map<Class<?>, CallableNode> parameterMap = namesMap.get(name);
            if(argList.size() == 0 && parameterMap.containsKey(null)) {
                candidates.add(parameterMap.get(null).getMethod());
            } else {
                Class<?> first = argList.pollFirst();
                if(first==null) {
                    List<Method> collect = parameterMap.values().stream().map(CallableNode::getMethod).collect(Collectors.toList());
                    candidates.addAll(collect);
                } else {
                    CallableNode callableNode = parameterMap.get(first);
                    if(callableNode != null) {
                        callableNode.findMethodCandidates(argList, candidates);
                    } else {
                        for(Class<?> cls: parameterMap.keySet()) {
                            if(cls.isAssignableFrom(first)) {
                                callableNode = parameterMap.get(cls);
                                parameterMap.put(first, callableNode);
                                callableNode.findMethodCandidates(argList, candidates);
                                return new HashSet<>(candidates);
                            }
                        }
                    }
                }
            }
        }
        return new HashSet<>(candidates);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallTree{");
        sb.append(namesMap);
        sb.append('}');
        return sb.toString();
    }
}
