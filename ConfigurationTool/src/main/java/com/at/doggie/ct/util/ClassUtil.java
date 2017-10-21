package com.at.doggie.ct.util;


import com.at.doggie.ct.dao.EntityField;
import com.at.doggie.ct.dao.EntityId;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public class ClassUtil {
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + "/" + packagePathSplit[i];
        }
        if (realClassLocation.startsWith("file:/")) {
            realClassLocation = realClassLocation.substring("file:/".length());
        }
        File packageDir = new File(realClassLocation);
        if (packageDir.isDirectory()) {
            String[] allClassName = packageDir.list();
            return covertToFullClassName(packageName, allClassName);
        }
        return null;
    }

    public static String[] getEntityIds(Class clazz) {
        List<String> entityIds = new LinkedList<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof EntityId) {
                    EntityId entityId = (EntityId) annotation;
                    entityIds.add(entityId.value());
                }
            }
        }
        return entityIds.toArray(new String[0]);
    }

    public static EntityFieldInfo getEntityFieldInfoByName(String fieldName, Class clazz) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return getEntityFieldInfo(field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EntityFieldInfo getEntityFieldInfo(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        EntityFieldInfo entityFieldInfo = new EntityFieldInfo();
        entityFieldInfo.entityName = field.getName();
        Annotation[] annotations = field.getAnnotations();
        boolean found = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof EntityId) {
                EntityId entityId = (EntityId) annotation;
                entityFieldInfo.isId = true;
                entityFieldInfo.modelName = entityId.value();
                found = true;
                break;
            } else if (annotation instanceof EntityField) {
                EntityField entityField = (EntityField) annotation;
                entityFieldInfo.modelName = entityField.value();
                found = true;
                break;
            }
        }
        return found ? entityFieldInfo : null;
    }

    private static String[] covertToFullClassName(String packageName, String []classNames) {
        if (null == classNames) {
            throw new IllegalArgumentException("Arugment not legal");
        }
        List<String> list = new ArrayList<String>(36);
        for (String className : classNames) {
            list.add(packageName + "." + className.substring(0, className.lastIndexOf(".class")));
        }
        return list.toArray(new String[0]);
    }

    public static class EntityFieldInfo {
        public boolean isId;
        public String modelName;
        public String entityName;
    }

    public static void main(String[] args) {
        // 1
//        String path = ClassUtil.class.getResource("/").toString().trim();
//        System.out.println(path);
//        for (String str : getPackageAllClassName(path, "com.at.registry.bean")) {
//            System.out.println(str);
//        }

        // 2
    }
}
