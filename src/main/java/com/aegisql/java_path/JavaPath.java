package com.aegisql.java_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Java path.
 */
public class JavaPath {

    private final static Logger LOG = LoggerFactory.getLogger(JavaPath.class);

    /**
     * The type Holder.
     */
    class Holder {
        /**
         * The Holder.
         */
        @PathElement("#")
        Object _holder_;
    }

    private Map<String,List<TypedPathElement>> cache = new HashMap<>();
    private final Class<?> aClass;
    private final CallTree callTree;
    private final ClassRegistry classRegistry;
    private final int pathNumber;
    private boolean enableCaching = false;

///Constructors

    private JavaPath(Class<?> aClass, ClassRegistry registry, Map<String,List<TypedPathElement>> cache, int pathNumber) {
        Objects.requireNonNull(aClass,"Builder class is null");
        this.aClass = aClass;
        this.callTree = CallTree.forClass(aClass,registry);
        this.classRegistry = registry;
        this.cache = cache;
        this.pathNumber = pathNumber;
    }

    /**
     * Instantiates a new Java path.
     *
     * @param aClass   the a class
     * @param registry the registry
     */
    public JavaPath(Class<?> aClass, ClassRegistry registry) {
        Objects.requireNonNull(aClass,"Builder class is null");
        this.aClass = aClass;
        this.callTree = CallTree.forClass(aClass,registry);
        this.classRegistry = registry;
        this.pathNumber = 0;
    }

    /**
     * Instantiates a new Java path.
     *
     * @param aClass the a class
     */
    public JavaPath(Class<?> aClass) {
        this(aClass,new ClassRegistry());
    }

    ///PUBLIC API

    /**
     * Init path object.
     *
     * @param path   the path
     * @param values array of values
     * @return the object
     */
    public Object initPath(String path, Object... values) {
        List<TypedPathElement> parse = pack(parse(path));
        return applyInHolder(parse,values);
    }

    /**
     * Init path t.
     *
     * @param <T>       the type parameter
     * @param rootClass the root class
     * @param path      the path
     * @param values    the values
     * @return the t
     */
    public <T> T initPath(Class<T> rootClass, String path, Object... values) {
        List<TypedPathElement> parse = pack(parse(path));
        parse.get(1).setType(rootClass.getName());
        return applyInHolder(parse,values);
    }

    /**
     * Eval path object.
     *
     * @param path   the path
     * @param root   the root
     * @param values the values
     * @return the value returned by the last path element call in the chain.
     */
    public Object evalPath(String path, Object root, Object... values) {
        List<TypedPathElement> parse = parse(path);
        ReferenceList backRefCollection = new ReferenceList(root);
        if(values == null || values.length == 0) {
            backRefCollection.addValue(null);
        } else if(values.length == 1) {
            LOG.debug("Applying value {} to path {}",backRefCollection,parse.stream().map(tpe->tpe==null?";":tpe.toString()).collect(Collectors.joining(".")));
            backRefCollection.addValue(values[0]);
        } else {
            LOG.debug("Applying multi-values {} to path {}",backRefCollection,parse.stream().map(tpe->tpe==null?";":tpe.toString()).collect(Collectors.joining(".")));
            Arrays.stream(values).forEach(backRefCollection::addValue);
        }
        return evalPath(parse,backRefCollection);
    }

    /**
     * Sets enable caching.
     *
     * @param enableCaching the enable caching
     */
    public void setEnableCaching(boolean enableCaching) {
        this.enableCaching = enableCaching;
    }

    //////////////////////

    private List<TypedPathElement> parse(String path) {
        if(enableCaching) {
            return cache.computeIfAbsent(path,p->JavaPathParser.parse(p));
        } else {
            return JavaPathParser.parse(path);
        }
    }

    private <T> T applyInHolder(List<TypedPathElement> path, Object... values) {
        Holder root = new Holder();
        JavaPath pu = new JavaPath(Holder.class, classRegistry, cache, pathNumber);
        pu.setEnableCaching(enableCaching);
        ReferenceList backRefCollection = new ReferenceList(root);
        if(values != null) {
            Arrays.stream(values).forEach(backRefCollection::addValue);
        }
        LOG.debug("Init from values {} path {}",backRefCollection,path.stream().map(tpe->tpe==null?";":tpe.toString()).collect(Collectors.joining(".")));
        pu.evalPath(path, backRefCollection);
        return (T) root._holder_;
    }

    private List<TypedPathElement> pack(List<TypedPathElement> path) {
        Objects.requireNonNull(path,"Requires path");
        if(path.size() == 0) {
            throw new JavaPathRuntimeException("Requires at least one path element");
        }
        List<TypedPathElement> newPath = new ArrayList<>();
        TypedPathElement rootPathElement = new TypedPathElement();
        rootPathElement.setName("");
        rootPathElement.setType(Holder.class.getName());
        TypedPathElement pathElement = path.get(0);
        pathElement.setName("#");
        newPath.add(rootPathElement);
        newPath.addAll(path);
        return newPath;
    }

    private <T> T evalPath(List<TypedPathElement> path, ReferenceList valuesRefsCollection) {
        Objects.requireNonNull(path,"Requires path");
        int size = path.size();
        if(size == 0) {
            return (T) valuesRefsCollection.getRoot(); //last calculated getter
        }
        TypedPathElement rootPathElement = path.get(0);

        if(rootPathElement == null) {
            ReferenceList nextReferenceList = valuesRefsCollection.startNextPath();
            JavaPath nextPath = new JavaPath(nextReferenceList.getRootClass(), classRegistry, cache, pathNumber+1);
            nextPath.setEnableCaching(enableCaching);
            return nextPath.evalPath(path.subList(1,path.size()),nextReferenceList);
        }

        boolean processAsSetter;
        if(size == 1) {
            processAsSetter = true;
        } else {
            processAsSetter = path.get(1) == null;
        }

        if(rootPathElement.parametrized()) {
            for(TypedValue tv:rootPathElement.getParameters()) {
                if(tv.hasPath()) {
                    if (tv.getBackRefIdx() >= 0) {
                        applyBackReference(valuesRefsCollection, tv);
                    } else if(tv.getValueIdx() >= 0) {
                        applyValueReference(valuesRefsCollection, tv);
                    }
                }
            }
        }
        if(rootPathElement.getOptionalPathElement() != null) {
            TypedPathElement optionalPathElement = rootPathElement.getOptionalPathElement();
            if(optionalPathElement.parametrized()) {
                for(TypedValue tv:optionalPathElement.getParameters()) {
                    if(tv.hasPath()) {
                        if (tv.getBackRefIdx() >= 0) {
                            applyBackReference(valuesRefsCollection, tv);
                        } else if(tv.getValueIdx() >= 0) {
                            applyValueReference(valuesRefsCollection, tv);
                        }
                    }
                }
            }
        }

        LOG.trace("Processing path element: {}; root object: {}",rootPathElement,valuesRefsCollection.getRoot());
        if(rootPathElement.getName().startsWith("@")) {
            if(rootPathElement.getType() == null) {
                return evalPath(path.subList(1,path.size()),valuesRefsCollection);
            } else {
                return applyAtSign(path, valuesRefsCollection, rootPathElement);
            }
        } else {
            if(processAsSetter) {
                T res =  (T) offerSetter(valuesRefsCollection, rootPathElement).apply(valuesRefsCollection);
                if(size == 1) {
                    return res;
                } else {
                    return evalPath(path.subList(1,size),valuesRefsCollection);
                }
            } else {
                return offerGetter(path, valuesRefsCollection, rootPathElement);
            }
        }
    }

    private <T> T applyAtSign(List<TypedPathElement> path, ReferenceList valuesRefsCollection, TypedPathElement rootPathElement) {
        Class refClass = classRegistry.classMap.get(rootPathElement.getType());
        if (refClass == null) {
            try {
                refClass = Class.forName(rootPathElement.getType());
            } catch (ClassNotFoundException e) {
                throw new JavaPathRuntimeException("Cannot find class for " + rootPathElement, e);
            }
        }
        JavaPath nextUtils = new JavaPath(refClass, classRegistry, cache, pathNumber);
        nextUtils.setEnableCaching(enableCaching);
        if (rootPathElement.getName().equalsIgnoreCase("@new")) {
            Function<ReferenceList, Object> constructor = nextUtils.offerConstructor(valuesRefsCollection, rootPathElement);
            if (constructor != null) {
                Object nextRef = constructor.apply(valuesRefsCollection);
                valuesRefsCollection.addReference(nextRef);
                return evalPath(path.subList(1, path.size()), valuesRefsCollection);
            }
            throw new JavaPathRuntimeException("Expected constructor of class " + refClass + " for " + rootPathElement);
        } else {
            rootPathElement.setName(rootPathElement.getName().substring(1));
            Function<ReferenceList, Object> getter = nextUtils.offerGetter(valuesRefsCollection, rootPathElement);
            if (getter != null) {
                Object nextRef = getter.apply(valuesRefsCollection);
                valuesRefsCollection.addReference(nextRef);
                return evalPath(path.subList(1, path.size()), valuesRefsCollection);
            }
            throw new JavaPathRuntimeException("Expected getter for class " + refClass + " for " + rootPathElement);
        }
    }

    private <T> T offerGetter(List<TypedPathElement> path, ReferenceList valuesRefsCollection, TypedPathElement rootPathElement) {
        Function<ReferenceList, Object> getter = offerGetter(valuesRefsCollection, rootPathElement);
        Object nextRoot =  getter.apply(valuesRefsCollection);
        if(rootPathElement.getOwnTypedValue().isPreEvaluatedValueSet()) {
            Class<?> nextClass = nextRoot == null ? classRegistry.classMap.get(rootPathElement.getType()) : nextRoot.getClass();
            JavaPath nextUtils = new JavaPath(nextClass, classRegistry, cache, pathNumber);
            nextUtils.setEnableCaching(enableCaching);
            valuesRefsCollection.addRoot(nextRoot);
            return nextUtils.evalPath(path.subList(1, path.size()), valuesRefsCollection);
        } else {
            if(nextRoot == null && rootPathElement.getOptionalPathElement() != null) {
                LOG.debug("Pathe element {} return null. Trying optional init {}",rootPathElement,rootPathElement.getOptionalPathElement());
                offerGetter(valuesRefsCollection, rootPathElement.getOptionalPathElement()).apply(valuesRefsCollection);
                nextRoot =  getter.apply(valuesRefsCollection);
            }
            Objects.requireNonNull(nextRoot, "Object for path element '" + rootPathElement + "' is not initialized!");
            JavaPath nextUtils = new JavaPath(nextRoot.getClass(), classRegistry, cache, pathNumber);
            nextUtils.setEnableCaching(enableCaching);
            valuesRefsCollection.addRoot(nextRoot);
            return nextUtils.evalPath(path.subList(1, path.size()), valuesRefsCollection);
        }
    }

    private void applyValueReference(ReferenceList valuesRefsCollection, TypedValue tv) {
        List<TypedPathElement> typedPathElements = new ArrayList<>(tv.getTypedPathElements());
        LOG.debug("ValueRef Value {} has own path that will be evaluated: {}", tv.getValue(), typedPathElements);
        Object pathRoot = valuesRefsCollection.getValue(tv.getValueIdx());
        Class<?> pathRootClass = pathRoot.getClass();
        ReferenceList rl = new ReferenceList(pathRoot);
        typedPathElements.get(typedPathElements.size() - 1).getOwnTypedValue().setPreEvaluatedValueSet(true);
        TypedPathElement term = new TypedPathElement();
        term.setName("@");
        typedPathElements.add(term);
        JavaPath javaPath = new JavaPath(pathRootClass, classRegistry, cache, pathNumber);
        tv.setPreEvaluatedValueSet(true);
        Object res = javaPath.evalPath(typedPathElements, rl);
        LOG.trace("ValueRef Evaluation result: {}", res);
        tv.setPreEvaluatedValue(res);
        tv.setType(res == null ? tv.getType() : res.getClass().getName());
        tv.setValueIdx(-1);
    }

    private void applyBackReference(ReferenceList valuesRefsCollection, TypedValue tv) {
        List<TypedPathElement> typedPathElements = new ArrayList<>(tv.getTypedPathElements());
        LOG.debug("BackRef Value {} has own path that will be evaluated: {}", tv.getValue(), typedPathElements);
        Object pathRoot = valuesRefsCollection.getReference(tv.getBackRefIdx());
        Class<?> pathRootClass = pathRoot.getClass();
        ReferenceList rl = new ReferenceList(pathRoot);
        valuesRefsCollection.getValues().forEach(rl::addValue);
        typedPathElements.get(typedPathElements.size() - 1).getOwnTypedValue().setPreEvaluatedValueSet(true);
        TypedPathElement term = new TypedPathElement();
        term.setName("@");
        typedPathElements.add(term);
        JavaPath javaPath = new JavaPath(pathRootClass, classRegistry, cache, pathNumber);
        tv.setPreEvaluatedValueSet(true);
        Object res = javaPath.evalPath(typedPathElements, rl);
        LOG.trace("BackRef Evaluation result: {}", res);
        tv.setPreEvaluatedValue(res);
        tv.setType(res == null ? tv.getType() : res.getClass().getName());
        tv.setBackRefIdx(-1);
    }

    private Function<ReferenceList,Object> offerConstructor(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry,javaPath);
        Constructor constructor = null;
        Class<?>[] classesForGetter = pl.getClassesForGetter(backReferences);
        Set<Constructor> constructors = callTree.findConstructorCandidates(classesForGetter);
            if(constructors.size() > 1) {
                throw new JavaPathRuntimeException("More than one constructor candidates found for "+pl+"; "+constructors);
            } else if(constructors.size() == 1) {
                constructor = constructors.iterator().next();
            }
        if(constructor != null) {
            LOG.trace("Constructor found {}",constructor);
            Constructor finalConstructor = constructor;
            return b->{
                Object[] propertiesForGetter = pl.getPropertiesForGetter(b);
                return invoke(finalConstructor, propertiesForGetter);
            };
        } else {
            String msg = Arrays.stream(classesForGetter).map(cls -> cls==null?"NULL":cls.getSimpleName()).collect(Collectors.joining(",", "[", "]"));
            LOG.trace("Constructor not found for classes {}.",msg);
            throw new JavaPathRuntimeException("Could not find constructor for "+javaPath);
        }
    }

    private Function<ReferenceList, Object> offerGetter(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry,javaPath);
        Method method = null;
        Class<?>[] classesForGetter = pl.getClassesForGetter(backReferences);
        Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),classesForGetter);
            if(methods.size() > 1) {
                throw new JavaPathRuntimeException("More than one getter method candidates found for "+pl+"; "+methods);
            } else if(methods.size() == 1) {
                method = methods.iterator().next();
            }
        if(method != null) {
            LOG.trace("Getter method found {}",method);
            Method finalMethod = method;
            return b->{
                Object[] propertiesForGetter = pl.getPropertiesForGetter(b);
                return invoke(finalMethod,b.getRoot(),propertiesForGetter);
            };
        } else {
            String label = pl.getLabel();
            if(LOG.isTraceEnabled()) {
                String msg = Arrays.stream(classesForGetter).map(cls -> cls==null?"NULL":cls.getSimpleName()).collect(Collectors.joining(",", "[", "]"));
                LOG.trace("Getter method not found for name '{}' and classes {}. Trying field", label, msg);
            }
            Field field = CallTree.forClass(aClass).findField(pl.getLabel());
            if(field == null) {
                LOG.trace("Field also not found. Using root of {}.class as getter.",backReferences.getRootClass().getSimpleName());
                return b->b.getRoot();
            } else {
                LOG.trace("Field found {}",field);
                return b->get(backReferences, pl,field,b.getRoot());
            }
        }
    }

    private <T> Function<ReferenceList,T> offerSetter(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry,javaPath);
        BiConsumer<Object,Object> setter;
        Method method = null;
        Class<?>[] classesForSetter = pl.getClassesForSetter(backReferences);
        Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),classesForSetter);
            if(methods.size() > 1) {
                throw new JavaPathRuntimeException("More than one setter method candidates found for "+pl+"; "+methods);
            } else if(methods.size() == 1) {
                method = methods.iterator().next();
            }
        if(method != null) {
            LOG.trace("Setter method found {}",method);
            Method finalMethod = method;
            if(method.getParameterCount() == 0) {
                return b-> (T) invoke(finalMethod,b.getRoot(),null);
            } else {
                return b-> (T) invoke(finalMethod, b.getRoot(), pl.getPropertiesForSetter(b));
            }
        } else {
            if(LOG.isTraceEnabled()) {
                String msg = Arrays.stream(classesForSetter).map(cls -> cls==null?"NULL":cls.getSimpleName()).collect(Collectors.joining(",", "[", "]"));
                LOG.trace("Setter method not found for name '{}' and classes {}. Trying field", pl.getLabel(),msg);
            }
            int pos = pl.getLabelProperties().size() == 0 ? pathNumber : pl.getLabelProperties().get(0).getValueIdx();
            Object value = null;
            if(pos >= 0) {
                value = backReferences.getValue(pos);
            } else {
                Object[] propertiesForSetter = pl.getPropertiesForSetter(backReferences);
                if(propertiesForSetter.length != 0) {
                    value = propertiesForSetter[0];
                }
            }
            Object finalValue = value;
            return b-> (T) fieldSetter(pl).apply(b, finalValue);
        }
    }

    private Object invoke(final Method method, Object builder, Object... params) {
        try {
            return setAccessible(method).invoke(builder, params);
        } catch (IllegalAccessException e) {
            throw new JavaPathRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private Object invoke(final Constructor constructor, Object... params) {
        try {
            return setAccessible(constructor).newInstance(params);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private Object get(ReferenceList backReferences, ParametrizedPath pl, final Field field, Object builder) {
        try {
            Object o = setAccessible(field).get(builder);
            if(o==null) {
                ParametrizedProperty labelProperty = pl.getParametrizedProperty();
                if(labelProperty.isPreEvaluatedValueSet()) {
                    Object preEvaluatedValue = labelProperty.getPreEvaluatedValue();
                    setAccessible(field).set(builder,preEvaluatedValue);
                    return preEvaluatedValue;
                }
                Class<?> fieldType;
                if(labelProperty.getTypeAlias() != null && labelProperty.getPropertyType() != null) {
                    fieldType = labelProperty.getPropertyType();
                } else {
                    fieldType = field.getType();
                }
                Class<?>[] classesForGetter = pl.getClassesForGetter(backReferences);
                Object[] propertiesForGetter = pl.getPropertiesForGetter(backReferences);
                CallTree.forClass(fieldType,classRegistry);
                String factory = pl.getPathElement().getFactory();

                if(factory == null || "new".equals(factory)) {
                    StringConverter stringConverter = classRegistry.getConverter(fieldType.getName(), fieldType.getSimpleName(), labelProperty.getTypeAlias(), "valueOf").orElse(null);
                    if (factory == null && classesForGetter.length == 1 && classesForGetter[0] == String.class && stringConverter != null) {
                        Object newInstance = stringConverter.apply(propertiesForGetter[0]);
                        setAccessible(field).set(builder, newInstance);
                        return newInstance;
                    } else {
                        Constructor<?> constructor = fieldType.getConstructor(classesForGetter);
                        Object newInstance = setAccessible(constructor).newInstance(propertiesForGetter);
                        setAccessible(field).set(builder, newInstance);
                        return newInstance;
                    }
                } else {
                    Method method = fieldType.getMethod(factory, classesForGetter);
                    Object newInstance = setAccessible(method).invoke(null, propertiesForGetter);
                    setAccessible(field).set(builder, newInstance);
                    return newInstance;
                }
            }
            return o;
        } catch (Exception e) {
            throw new JavaPathRuntimeException("Failed instantiating "+pl,e);
        }
    }

    private void set(final Field field, Object builder, Object val) {
        try {
            setAccessible(field).set(builder,val);
        } catch (IllegalAccessException e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private <T> BiFunction<ReferenceList, Object,T> fieldSetter(ParametrizedPath pl) {
        String label = pl.getLabel();
        Field field = CallTree.forClass(aClass).findField(pl.getLabel());
        if(field != null) {
            LOG.trace("Field for setter found {}",field);
            return (b, v)->{
                set(field, b.getRoot(), v);
                return (T) v;
            };
        }
        LOG.trace("Field for setter not found for name '{}'",label);
        return (b,v)->{throw new JavaPathRuntimeException("No setter found for "+label);};
    }

    //////////////////////

    /**
     * Sets accessible.
     *
     * @param constructor the constructor
     * @return the accessible
     */
    public static Constructor setAccessible(Constructor constructor) {
        int classModifiers  = constructor.getDeclaringClass().getModifiers();
        int constructorModifiers = constructor.getModifiers();
        if( ! Modifier.isPublic(constructorModifiers) || Modifier.isAbstract(classModifiers) || Modifier.isFinal(classModifiers)) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    /**
     * Sets accessible.
     *
     * @param method the method
     * @return the accessible
     */
    public static Method setAccessible(Method method) {
        int classModifiers  = method.getDeclaringClass().getModifiers();
        int methodModifiers = method.getModifiers();
        if( ! Modifier.isPublic(methodModifiers) || Modifier.isAbstract(classModifiers) || Modifier.isFinal(classModifiers)) {
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * Sets accessible.
     *
     * @param field the field
     * @return the accessible
     */
    public static Field setAccessible(Field field) {
        int classModifiers = field.getDeclaringClass().getModifiers();
        int fieldModifiers = field.getModifiers();
        if( ! Modifier.isPublic(fieldModifiers) || Modifier.isFinal(fieldModifiers) || Modifier.isAbstract(classModifiers) || Modifier.isFinal(classModifiers)) {
            field.setAccessible(true);
        }
        return field;
    }

}
