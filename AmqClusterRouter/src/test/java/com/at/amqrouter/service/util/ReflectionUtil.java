package com.at.amqrouter.service.util;

import java.lang.reflect.Field;

/**
 * Created by Terry LIANG on 2017/10/2.
 */
public class ReflectionUtil {
    public static void setPropertyByReflection(Object object, String key, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(key);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getPropertyByReflection(Object object, String key) {
        try {
            Field field = object.getClass().getDeclaredField(key);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
