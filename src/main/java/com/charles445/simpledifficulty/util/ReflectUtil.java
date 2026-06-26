package com.charles445.simpledifficulty.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtil {
    // Cache for methods and fields to avoid repeated lookups
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();
    
    public static Method findMethod(Class<?> clazz, String name) throws Exception {
        String cacheKey = clazz.getName() + "#" + name;
        Method cached = METHOD_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Desc is not specified
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                m.setAccessible(true);
                METHOD_CACHE.put(cacheKey, m);
                return m;
            }
        }
        
        throw new NoSuchMethodException(name);
    }
    
    public static Method findMethodAny(Class<?> clazz, String nameA, String nameB, Class<?>... params) throws Exception {
        // Try nameA first without exception overhead
        try {
            return findMethod(clazz, nameA, params);
        } catch (Exception e) {
            return findMethod(clazz, nameB, params);
        }
    }
    
    public static Method findMethod(Class<?> clazz, String name, Class<?>... params) throws Exception {
        String cacheKey = clazz.getName() + "#" + name + Arrays.toString(params);
        Method cached = METHOD_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        Method m = clazz.getDeclaredMethod(name, params);
        m.setAccessible(true);
        METHOD_CACHE.put(cacheKey, m);
        return m;
    }
    
    public static Field findField(Class<?> clazz, String name) throws Exception {
        String cacheKey = clazz.getName() + "#" + name;
        Field cached = FIELD_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        FIELD_CACHE.put(cacheKey, f);
        return f;
    }
    
    public static Field findFieldAny(Class<?> clazz, String nameA, String nameB) throws Exception {
        try {
            return findField(clazz, nameA);
        } catch (Exception e) {
            return findField(clazz, nameB);
        }
    }
    
    public static Field[] findFields(Class<?> clazz, String... names) throws Exception {
        Field[] fields = new Field[names.length];
        
        for (int i = 0; i < fields.length; i++) {
            fields[i] = findField(clazz, names[i]);
        }
        
        return fields;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] createCastedArray(Class<T> clazz, Object[] inputArray) {
        T[] newArray = (T[]) Array.newInstance(clazz, inputArray.length);
        System.arraycopy(inputArray, 0, newArray, 0, inputArray.length);
        return newArray;
    }
}
