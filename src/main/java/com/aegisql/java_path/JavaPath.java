package com.aegisql.java_path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaPath {

    private final static Logger LOG = LoggerFactory.getLogger(JavaPath.class);

    static class Holder {
        @Label("#")
        Object _holder_;
    }

    private Map<String,List<TypedPathElement>> cache = new HashMap<>();
    private final Class<?> aClass;
    private final CallTree callTree;
    private final ClassRegistry classRegistry;
    private boolean enableCaching = false;


    private JavaPath(Class<?> aClass, ClassRegistry registry, Map<String,List<TypedPathElement>> cache) {
        Objects.requireNonNull(aClass,"Builder class is null");
        this.aClass = aClass;
        this.callTree = CallTree.forClass(aClass);
        this.classRegistry = registry;
        this.cache = cache;
    }

    public JavaPath(Class<?> aClass, ClassRegistry registry) {
        Objects.requireNonNull(aClass,"Builder class is null");
        this.aClass = aClass;
        this.callTree = CallTree.forClass(aClass);
        this.classRegistry = registry;
    }

    public JavaPath(Class<?> aClass) {
        this(aClass,new ClassRegistry());
    }

    private List<TypedPathElement> parse(String path) {
        if(enableCaching) {
            return cache.computeIfAbsent(path,p->JavaPathParser.parse(p));
        } else {
            return JavaPathParser.parse(path);
        }
    }

    public Object initObjectFromPath(String path, Object value) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,value);
    }

    public Object initObjectFromPath(String path, Object value, Object value2, Object... more) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,value, value2, more);
    }

    public Object initObjectFromPath(String path, Collection<Object> values) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,values);
    }

    public <T> T initObjectFromPath(String path, Class<T> rootClass, Object value) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,rootClass,value);
    }

    public <T> T initObjectFromPath(String path, Class<T> rootClass, Object value, Object value2, Object... more) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,rootClass,value,value2,more);
    }

    public <T> T initObjectFromPath(String path, Class<T> rootClass, Collection<Object> values) {
        List<TypedPathElement> parse = parse(path);
        return initObjectFromPath(parse,rootClass,values);
    }

    ////
    public Object initObjectFromPath(List<TypedPathElement> path, Object value) {
        List<TypedPathElement> newPath = pack(path);
        return applyInHolder(newPath,value);
    }

    public Object initObjectFromPath(List<TypedPathElement> path, Object value, Object value2, Object... more) {
        List<TypedPathElement> newPath = pack(path);
        return applyInHolder(newPath,value,value2,more);
    }

    public Object initObjectFromPath(List<TypedPathElement> path, Collection<Object> values) {
        List<TypedPathElement> newPath = pack(path);
        return applyInHolder(newPath,values);
    }

    public <T> T initObjectFromPath(List<TypedPathElement> path, Class<T> rootClass, Object value) {
        List<TypedPathElement> newPath = pack(path);
        newPath.get(1).setType(rootClass.getName());
        return applyInHolder(newPath,value);
    }

    public <T> T initObjectFromPath(List<TypedPathElement> path, Class<T> rootClass, Object value, Object value2, Object... more) {
        List<TypedPathElement> newPath = pack(path);
        newPath.get(1).setType(rootClass.getName());
        return applyInHolder(newPath,value,value2,more);
    }

    public <T> T initObjectFromPath(List<TypedPathElement> path, Class<T> rootClass, Collection<Object> values) {
        List<TypedPathElement> newPath = pack(path);
        newPath.get(1).setType(rootClass.getName());
        return applyInHolder(newPath,values);
    }

    private <T> T applyInHolder(List<TypedPathElement> path, Object value) {
        Holder root = new Holder();
        JavaPath pu = new JavaPath(Holder.class, classRegistry, cache);
        pu.setEnableCaching(enableCaching);
        ReferenceList backRefCollection = new ReferenceList(root,value);
        pu.applyValueToPath(path, backRefCollection);
        LOG.debug("Init from value {} path {}",backRefCollection,path.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        return (T) root._holder_;
    }

    private <T> T applyInHolder(List<TypedPathElement> path, Object value, Object value2, Object... more) {
        Holder root = new Holder();
        JavaPath pu = new JavaPath(Holder.class, classRegistry, cache);
        pu.setEnableCaching(enableCaching);
        ReferenceList backRefCollection = new ReferenceList(root,value);
        backRefCollection.addValue(value2);
        if(more != null) {
            Arrays.stream(more).forEach(backRefCollection::addValue);
        }
        LOG.debug("Init from multi-values {} path {}",backRefCollection,path.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        pu.applyValueToPath(path, backRefCollection);
        return (T) root._holder_;
    }

    private <T> T applyInHolder(List<TypedPathElement> path, Collection<Object> values) {
        Holder root = new Holder();
        JavaPath pu = new JavaPath(Holder.class, classRegistry, cache);
        pu.setEnableCaching(enableCaching);
        ReferenceList backRefCollection = new ReferenceList(root);
        if(values != null) {
            values.stream().forEach(backRefCollection::addValue);
        }
        LOG.debug("Init from multi-values {} path {}",backRefCollection,path.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        pu.applyValueToPath(path, backRefCollection);
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

    public Object applyValuesToPath(String path, Object root, Collection<Object> values) {
        List<TypedPathElement> parse = parse(path);
        ReferenceList backRefCollection = new ReferenceList(root);
        if(values == null && values.size() == 0) {
            backRefCollection.addValue(null);
        } else {
            values.forEach(backRefCollection::addValue);
        }
        LOG.debug("Applying multi-values {} to path {}",backRefCollection,parse.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        return applyValueToPath(parse,backRefCollection);
    }

    public Object applyValuesToPath(String path, Object root, Object... values) {
        List<TypedPathElement> parse = parse(path);
        ReferenceList backRefCollection = new ReferenceList(root);
        if(values == null && values.length == 0) {
            backRefCollection.addValue(null);
        } else {
            Arrays.stream(values).forEach(backRefCollection::addValue);
        }
        LOG.debug("Applying multi-values {} to path {}",backRefCollection,parse.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        return applyValueToPath(parse,backRefCollection);
    }

    public Object applyValueToPath(String path, Object root, Object value) {
        List<TypedPathElement> parse = parse(path);
        ReferenceList backRefCollection = new ReferenceList(root,value);
        LOG.debug("Applying value {} to path {}",backRefCollection,parse.stream().map(TypedPathElement::toString).collect(Collectors.joining(".")));
        return applyValueToPath(parse,backRefCollection);
    }

    private <T> T applyValueToPath(List<TypedPathElement> path, ReferenceList valuesRefsCollection) {
        Objects.requireNonNull(path,"Requires path");
        int size = path.size();
        if(size == 0) {
            return null;
        }
        Object root = valuesRefsCollection.getRoot();
        Objects.requireNonNull(root,"Requires root object");
        TypedPathElement rootPathElement = path.get(0);
        LOG.trace("Processing path element: {}; root object: {}",rootPathElement,root);
        if(rootPathElement.getName().startsWith("@")) {
            if(rootPathElement.getType() == null) {
                return applyValueToPath(path.subList(1,path.size()),valuesRefsCollection);
            }
            Class refClass = classRegistry.classMap.get(rootPathElement.getType());
            if(refClass == null) {
                try {
                    refClass = Class.forName(rootPathElement.getType());
                } catch (ClassNotFoundException e) {
                    throw new JavaPathRuntimeException("Cannot find class for "+rootPathElement,e);
                }
            }
            JavaPath nextUtils = new JavaPath(refClass, classRegistry, cache);
            nextUtils.setEnableCaching(enableCaching);
            if(rootPathElement.getName().equalsIgnoreCase("@new")) {
                Function<ReferenceList, Object> constructor = nextUtils.offerConstructor(valuesRefsCollection, rootPathElement);
                if (constructor != null) {
                    Object nextRef = constructor.apply(valuesRefsCollection);
                    valuesRefsCollection.addReference(nextRef);
                    return applyValueToPath(path.subList(1, path.size()), valuesRefsCollection);
                }
                throw new JavaPathRuntimeException("Expected constructor of class "+refClass+" for "+rootPathElement );
            } else {
                rootPathElement.setName(rootPathElement.getName().substring(1));
                Function<ReferenceList, Object> getter = nextUtils.offerGetter(valuesRefsCollection, rootPathElement);
                if (getter != null) {
                    Object nextRef = getter.apply(valuesRefsCollection);
                    valuesRefsCollection.addReference(nextRef);
                    return applyValueToPath(path.subList(1, path.size()), valuesRefsCollection);
                }
                throw new JavaPathRuntimeException("Expected getter for class "+refClass+" for "+rootPathElement );
            }
        } else {
            if(size == 1) {
                return (T) offerSetter(valuesRefsCollection, rootPathElement).apply(valuesRefsCollection);
            } else {
                Function<ReferenceList, Object> getter = offerGetter(valuesRefsCollection, rootPathElement);
                Object nextRoot =  getter.apply(valuesRefsCollection);
                Objects.requireNonNull(nextRoot,"Object for path element '"+rootPathElement+"' is not initialized!");
                JavaPath nextUtils = new JavaPath(nextRoot.getClass(), classRegistry, cache);
                nextUtils.setEnableCaching(enableCaching);
                valuesRefsCollection.addRoot(nextRoot);
                return nextUtils.applyValueToPath(path.subList(1,path.size()),valuesRefsCollection);
            }
        }
    }

    public Function<ReferenceList,Object> offerConstructor(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        Constructor constructor = null;
          Set<Constructor> constructors = callTree.findConstructorCandidates(pl.getClassesForGetter(backReferences));
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
            throw new JavaPathRuntimeException("Could not find constructor for "+javaPath);
        }
    }

    public Function<ReferenceList, Object> offerGetter(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        Method method = null;
            Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),pl.getClassesForGetter(backReferences));
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
            LOG.trace("Getter method not found for {}. Trying field",label);
            Field field = CallTree.forClass(aClass).findField(pl.getLabel());
            if(field == null) {
                LOG.trace("Field also not found. Using root as getter.");
                return b->b.getRoot();
            } else {
                LOG.trace("Field found {}",field);
                return b->get(backReferences, pl,field,b.getRoot());
            }
        }
    }

    public <T> Function<ReferenceList,T> offerSetter(ReferenceList backReferences, TypedPathElement javaPath) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        BiConsumer<Object,Object> setter;
        Method method = null;
            Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),pl.getClassesForSetter(backReferences));
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
            LOG.trace("Setter method not found for {}. Trying field",pl.getLabel());
            return b-> (T) fieldSetter(pl).apply(b,b.getValue(0));
        }
    }

///////////////////////////////////////////

    private Object invoke(final Method method, Object builder, Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(builder, params);
        } catch (IllegalAccessException e) {
            throw new JavaPathRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private Object invoke(final Constructor constructor, Object... params) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new JavaPathRuntimeException(e);
        }
    }

    private Object get(ReferenceList backReferences, ParametrizedPath pl, final Field field, Object builder) {
        try {
            field.setAccessible(true);
            Object o = field.get(builder);
            if(o==null) {
                ParametrizedProperty labelProperty = pl.getParametrizedProperty();
                Class<?> fieldType;
                if(labelProperty.getTypeAlias() != null && labelProperty.getPropertyType() != null) {
                    fieldType = labelProperty.getPropertyType();
                } else {
                    fieldType = field.getType();
                }
                Class<?>[] classesForGetter = pl.getClassesForGetter(backReferences);
                Constructor<?> constructor = fieldType.getConstructor(classesForGetter);
                Object[] propertiesForGetter = pl.getPropertiesForGetter(backReferences);
                constructor.setAccessible(true);
                Object newInstance = constructor.newInstance(propertiesForGetter);
                field.setAccessible(true);
                field.set(builder,newInstance);
                return newInstance;
            }
            return o;
        } catch (Exception e) {
            throw new JavaPathRuntimeException("Failed instantiating "+pl,e);
        }
    }

    private void set(final Field field, Object builder, Object val) {
        try {
            field.setAccessible(true);
            field.set(builder,val);
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
        LOG.trace("Field for setter not found");
        return (b,v)->{throw new JavaPathRuntimeException("No setter found for "+label);};
    }

    public void setEnableCaching(boolean enableCaching) {
        this.enableCaching = enableCaching;
    }

}
