package org.xxxx.utils;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings ( "restriction" )
public class Reflections {

    public static Object invokeMethod(Object object, Class clazz, String methodName, Class[] paramTypes, Object... parameters) {
        try {
            Method method = clazz.getMethod(methodName, paramTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            return method.invoke(object, parameters);
        } catch (Exception var7) {
            String message = "Reflection call " + methodName + " failed: " + var7.getMessage();
            if (clazz != null) {
                message = "Reflection call " + clazz.getName() + "." + methodName + " failed: " + var7.getMessage();
            }
            return null;
        }
    }
    public static Object invokeMethod(Object object, String methodName, Class[] paramTypes, Object... parameters) {
        return object == null ? null : invokeMethod(object, object.getClass(), methodName, paramTypes, parameters);
    }
    public static String invokeStringMethod(Object object, String methodName, Class[] paramTypes, Object... parameters) {
        Object ret = invokeMethod(object, methodName, paramTypes, parameters);
        return ret != null ? (String)ret : null;
    }

    public static Object getField(final Object object, final String fieldName) throws Exception {
        Field field = null;
        Class clazz = object.getClass();
        while(clazz != Object.class){
            try{
                field = clazz.getDeclaredField(fieldName);
                break;
            }catch (NoSuchFieldException exception){
                clazz = clazz.getSuperclass();
            }
        }
        if(field == null){
            throw new NoSuchMethodException(fieldName);
        }
        else {
            field.setAccessible(true);
            return field.get(object);
        }
    }


    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Object getFieldValue(final Object obj, final String fieldName) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }

    public static Constructor<?> getFirstCtor(final String name) throws Exception {
        final Constructor<?> ctor = Class.forName(name).getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor;
    }

    public static Object newInstance(String className, Object ... args) throws Exception {
        return getFirstCtor(className).newInstance(args);
    }

    public static <T> T createWithoutConstructor ( Class<T> classToInstantiate )
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return createWithConstructor(classToInstantiate, Object.class, new Class[0], new Object[0]);
    }

    @SuppressWarnings ( {"unchecked"} )
    public static <T> T createWithConstructor ( Class<T> classToInstantiate, Class<? super T> constructorClass, Class<?>[] consArgTypes, Object[] consArgs )
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? super T> objCons = constructorClass.getDeclaredConstructor(consArgTypes);
        objCons.setAccessible(true);
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(classToInstantiate, objCons);
        sc.setAccessible(true);
        return (T)sc.newInstance(consArgs);
    }

}