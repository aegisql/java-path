package com.aegisql.java_path;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class PathUtils {

    static class Holder {
        public Holder(){}
        @Label("#")
        Object _holder_;
    }

    private final Class<?> aClass;
    private final CallTree callTree;
    private final ClassRegistry classRegistry;
    private final Map<String, Field> fieldsByName = new HashMap<>();
    private final Map<TypedPathElement,BiFunction<Object,Object,Object> > getters  = new HashMap<>();
    private final Map<TypedPathElement,BiConsumer<Object,Object> > setters  = new HashMap<>();
    private boolean enableAccessorsCaching = false;


    public PathUtils(Class<?> aClass,ClassRegistry registry) {
        Objects.requireNonNull(aClass,"Builder class is null");
        this.aClass = aClass;
        this.callTree = CallTree.forClass(aClass);
        this.classRegistry = registry;
        seedFields(aClass);
    }

    public PathUtils(Class<?> aClass) {
        this(aClass,new ClassRegistry());
    }

    public Object initObjectFromPath(String path, Object value) {
        List<TypedPathElement> parse = JavaPathParser.parse(path);
        return initObjectFromPath(parse,value);
    }

    public Object initObjectFromPath(List<TypedPathElement> path, Object value) {
        Objects.requireNonNull(path,"Requires path");
        if(path.size() == 0) {
            throw new JavaPathRuntimeException("Requires at least one path element");
        }
        TypedPathElement rootPathElement = new TypedPathElement();
        rootPathElement.setName("");
        rootPathElement.setType(Holder.class.getName());
        TypedPathElement pathElement = path.get(0);
        pathElement.setName("#");
        List<TypedPathElement> newPath = new ArrayList<>();
        newPath.add(rootPathElement);
        newPath.addAll(path);
        Holder root = new Holder();
        PathUtils pu = new PathUtils(Holder.class, classRegistry);
        ReferenceList backRefCollection = new ReferenceList(root);
        pu.applyValueToPath(newPath, backRefCollection, value);
        return root._holder_;
    }

    public <T> T initObjectFromPath(String path, Class<T> rootClass, Object value) {
        List<TypedPathElement> parse = JavaPathParser.parse(path);
        return initObjectFromPath(parse,rootClass,value);
    }

    public <T> T initObjectFromPath(List<TypedPathElement> path, Class<T> rootClass, Object value) {
        Objects.requireNonNull(path,"Requires path");
        if(path.size() == 0) {
            throw new JavaPathRuntimeException("Requires at least one path element");
        }
        TypedPathElement rootPathElement = new TypedPathElement();
        rootPathElement.setName("");
        rootPathElement.setType(Holder.class.getName());
        TypedPathElement pathElement = path.get(0);
        pathElement.setName("#");
        pathElement.setType(rootClass.getName());
        List<TypedPathElement> newPath = new ArrayList<>();
        newPath.add(rootPathElement);
        newPath.addAll(path);
        Holder root = new Holder();
        PathUtils pu = new PathUtils(Holder.class, classRegistry);
        ReferenceList backRefCollection = new ReferenceList(root);
        pu.applyValueToPath(newPath, backRefCollection, value);
        return (T) root._holder_;
    }


    public Object applyValueToPath(String path, Object root, Object value) {
        List<TypedPathElement> parse = JavaPathParser.parse(path);
        ReferenceList backRefCollection = new ReferenceList(root);
        return applyValueToPath(parse,backRefCollection,value);
    }

    private <T> T applyValueToPath(List<TypedPathElement> path, ReferenceList backRefCollection, Object value) {
        Objects.requireNonNull(path,"Requires path");
        int size = path.size();
        if(size == 0) {
            return null;
        }
        Object root = backRefCollection.getRoot();
        Objects.requireNonNull(root,"Requires root object");
        TypedPathElement rootPathElement = path.get(0);
        Class<?> vClass = value == null ? null : value.getClass();

        if(rootPathElement.getName().startsWith("@")) {
            if(rootPathElement.getType() == null) {
                return applyValueToPath(path.subList(1,path.size()),backRefCollection,value);
            }
            Class refClass = classRegistry.classMap.get(rootPathElement.getType());
            if(refClass == null) {
                try {
                    refClass = Class.forName(rootPathElement.getType());
                } catch (ClassNotFoundException e) {
                    throw new JavaPathRuntimeException("Cannot find class for "+rootPathElement,e);
                }
            }
            PathUtils nextUtils = new PathUtils(refClass,classRegistry);
            nextUtils.setEnableAccessorsCaching(enableAccessorsCaching);
            if(rootPathElement.getName().equalsIgnoreCase("@new")) {
                BiFunction<ReferenceList, Object, Object> constructor = nextUtils.offerConstructor(backRefCollection, rootPathElement, vClass);
                if (constructor != null) {
                    Object nextRef = constructor.apply(backRefCollection, value);
                    backRefCollection.addReference(nextRef);
                    return applyValueToPath(path.subList(1, path.size()), backRefCollection, value);
                }
                throw new JavaPathRuntimeException("Expected constructor of class "+refClass+" for "+rootPathElement );
            } else {
                rootPathElement.setName(rootPathElement.getName().substring(1));
                BiFunction<ReferenceList, Object, Object> getter = nextUtils.offerGetter(backRefCollection, rootPathElement, vClass);
                if (getter != null) {
                    Object nextRef = getter.apply(backRefCollection, value);
                    backRefCollection.addReference(nextRef);
                    return applyValueToPath(path.subList(1, path.size()), backRefCollection, value);
                }
                throw new JavaPathRuntimeException("Expected getter for class "+refClass+" for "+rootPathElement );
            }
        } else {
            if(size == 1) {
                return (T) offerSetter(backRefCollection, rootPathElement,vClass).apply(backRefCollection,value);
            } else {
                BiFunction<ReferenceList, Object, Object> getter = offerGetter(backRefCollection, rootPathElement, vClass);
                Object nextRoot =  getter.apply(backRefCollection, value);
                Objects.requireNonNull(nextRoot,"Object for path element '"+rootPathElement+"' is not initialized!");
                PathUtils nextUtils = new PathUtils(nextRoot.getClass(),classRegistry);
                nextUtils.setEnableAccessorsCaching(enableAccessorsCaching);
                backRefCollection.addRoot(nextRoot);
                return nextUtils.applyValueToPath(path.subList(1,path.size()),backRefCollection,value);
            }
        }
    }

    public BiFunction<ReferenceList,Object,Object> offerConstructor(ReferenceList backReferences, TypedPathElement javaPath, Class vClass) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        Constructor constructor = null;
        if(vClass != null || pl.hasValueType()) {
            constructor = callTree.findConstructor(pl.getClassesForGetter(backReferences,vClass));
        } else {
            Set<Constructor> constructors = callTree.findConstructorCandidates(pl.getClassesForGetter(backReferences,vClass));
            if(constructors.size() > 1) {
                throw new JavaPathRuntimeException("More than one constructor candidates found for "+pl+"; "+constructors);
            } else if(constructors.size() == 1) {
                constructor = constructors.iterator().next();
            }
        }
        if(constructor != null) {
            Constructor finalConstructor = constructor;
            return (b, v)->{
                Object[] propertiesForGetter = pl.getPropertiesForGetter(b, v);
                return invoke(finalConstructor, propertiesForGetter);
            };
        } else {
            throw new JavaPathRuntimeException("Could not find constructor for "+javaPath);
        }
    }

    public BiFunction<ReferenceList, Object,Object> offerGetter(ReferenceList backReferences, TypedPathElement javaPath, Class vClass) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        Method method = null;
        if(vClass != null || pl.hasValueType()) {
            method = callTree.findMethod(pl.getLabel(),pl.getClassesForGetter(backReferences,vClass));
        } else {
            Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),pl.getClassesForGetter(backReferences,vClass));
            if(methods.size() > 1) {
                throw new JavaPathRuntimeException("More than one getter method candidates found for "+pl+"; "+methods);
            } else if(methods.size() == 1) {
                method = methods.iterator().next();
            }
        }
        if(method != null) {
            Method finalMethod = method;
            return (b, v)->{
                Object[] propertiesForGetter = pl.getPropertiesForGetter(b, v);
                return invoke(finalMethod,b.getRoot(),propertiesForGetter);
            };
        } else {
            String label = pl.getLabel();
            Field field = fieldsByName.get(label);
            if(field == null) {
                return (b,v)->b.getRoot();
            } else {
                return (b,v)->get(backReferences, pl,field,b.getRoot());
            }
        }
    }

    public <T> BiFunction<ReferenceList,Object,T> offerSetter(ReferenceList backReferences, TypedPathElement javaPath, Class<?> vClass) {
        ParametrizedPath pl = new ParametrizedPath(classRegistry, aClass,javaPath);
        BiConsumer<Object,Object> setter;
        Method method = null;
        if(vClass != null || pl.hasValueType()) {
            method = callTree.findMethod(pl.getLabel(),pl.getClassesForSetter(backReferences,vClass));
        } else {
            Set<Method> methods = callTree.findMethodCandidates(pl.getLabel(),pl.getClassesForSetter(backReferences,vClass));
            if(methods.size() > 1) {
                throw new JavaPathRuntimeException("More than one setter method candidates found for "+pl+"; "+methods);
            } else if(methods.size() == 1) {
                method = methods.iterator().next();
            }
        }
        if(method != null) {
            Method finalMethod = method;
            if(method.getParameterCount() == 0) {
                return (b, v)-> (T) invoke(finalMethod,b.getRoot(),null);
            } else {
                return (b, v) -> (T) invoke(finalMethod, b.getRoot(), pl.getPropertiesForSetter(b, v));
            }
        } else {
            return fieldSetter(pl);
        }
    }

///////////////////////////////////////////

    private void seedFields(Class bClass) {
        Label label;
        NoLabel noLabel;
        String name;
        for (Field f : bClass.getDeclaredFields()) {
            label = f.getAnnotation(Label.class);
            noLabel = f.getAnnotation(NoLabel.class);
            name = f.getName();
            setField(name,f);
            if(label != null) {
                if(noLabel != null) {
                    throw new JavaPathRuntimeException("Field " + f + " annotated with both @Label and @NoLabel. Please remove one.");
                } else {
                    for (String lbl : label.value()) {
                        setField(lbl,f);
                    }
                }
            }
        }
        Class sClass = bClass.getSuperclass();
        if(sClass != null) {
            seedFields(sClass);
        }
    }

    private void setField(String name, Field f){
        if ( ! fieldsByName.containsKey(name) ) {
            fieldsByName.put(name, f);
        }
    }

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
                Class<?>[] classesForGetter = pl.getClassesForGetter(backReferences, null);
                Constructor<?> constructor = fieldType.getConstructor(classesForGetter);
                Object[] propertiesForGetter = pl.getPropertiesForGetter(backReferences, null);
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
        Field field = fieldsByName.get(pl.getLabel());
        if(field != null) {
            return (b, v)->{
                set(field, b.getRoot(), v);
                return (T) v;
            };
        }
        return (b,v)->{throw new JavaPathRuntimeException("No setter found for "+label);};
    }

    public void setEnableAccessorsCaching(boolean enableAccessorsCaching) {
        this.enableAccessorsCaching = enableAccessorsCaching;
    }

}
