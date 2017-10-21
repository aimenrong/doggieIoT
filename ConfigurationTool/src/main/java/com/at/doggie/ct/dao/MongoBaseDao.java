package com.at.doggie.ct.dao;

import com.alibaba.fastjson.JSON;
import com.at.doggie.ct.exception.MongoDaoException;
import com.at.doggie.ct.util.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.List;


/**
 * Created by Terry LIANG on 2017/9/23.
 */
public class MongoBaseDao<T> {
    @Autowired
    private MongoOperations mongoOperations;
    private static final String BEAN_PACKAGE_PATH = "com.at.doggie.ct.bean";

    public void init() {
        try {
            String path = MongoBaseDao.class.getResource("/").toString().trim();
            String[] beanClassNames = ClassUtil.getPackageAllClassName(path, BEAN_PACKAGE_PATH);
            if (beanClassNames != null) {
                for (String beanClassName : beanClassNames) {
                    Class beanClass = Class.forName(beanClassName);
                    Entity entityAnnotation = (Entity) beanClass.getAnnotation(Entity.class);
                    if (entityAnnotation != null) {
                        if (!mongoOperations.collectionExists(entityAnnotation.value())) {
                            mongoOperations.createCollection(entityAnnotation.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void insert(T t) throws MongoDaoException {
        try {
//            mongoOperations.insert(t);
            mongoOperations.save(t);
        } catch (Exception e) {
            throw new MongoDaoException(e);
        }
    }

    public void update(T t, boolean createIfNotExist) throws MongoDaoException {
       try {
           Query query = null;
           Criteria criteria = new Criteria();
           Update update = new Update();
           Field[] fields = t.getClass().getDeclaredFields();
           for (int i = 0; i < fields.length; i++) {
               Field field = fields[i];
               ClassUtil.EntityFieldInfo entityFieldInfo = ClassUtil.getEntityFieldInfo(field);
               if (entityFieldInfo != null) {
                   if (entityFieldInfo.isId) {
                       criteria.and(entityFieldInfo.modelName).is(field.get(t));
                   }
                   update.set(entityFieldInfo.modelName, field.get(t));
               }
           }
           query = Query.query(criteria);
           Object t2 = mongoOperations.findAndModify(query, update, t.getClass());

           if (createIfNotExist && t2 == null) {
               insert(t);
           }
       } catch (Exception e) {
           throw new MongoDaoException(e);
       }
    }

    public T findOneByParam(String paramName, Object paramValue, Class<T> clazz) throws MongoDaoException {
        List<T> list = findByParam(paramName, paramValue, clazz);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    protected T findOneByParams(String[] paramNames, Object[] paramValues, Class<T> clazz) throws MongoDaoException {
        List<T> list = findByParams(paramNames, paramValues, clazz);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    protected List<T> findByParam(String paramName, Object paramValue, Class<T> clazz) throws MongoDaoException {
        return findByParams(new String[]{paramName}, new Object[]{paramValue}, clazz);
    }

    public List<T> findByParams(String[] paramNames, Object[] paramValues, Class<T> clazz) throws MongoDaoException {
       try {
           Query query = null;
           Criteria criteria = new Criteria();
           for (int i = 0; i < paramNames.length; i++) {
               String fieldName = paramNames[i];
               ClassUtil.EntityFieldInfo entityFieldInfo = ClassUtil.getEntityFieldInfoByName(fieldName, clazz);
               if (entityFieldInfo != null) {
                   if ("meta".equals(fieldName)) {
                       String convertValue = JSON.toJSONString(paramValues[i]);
                       criteria.and(entityFieldInfo.modelName).is(convertValue);
                   } else {
                       criteria.and(entityFieldInfo.modelName).is(paramValues[i]);
                   }
               }
           }
           query = Query.query(criteria);
           return mongoOperations.find(query, clazz);
       } catch (Exception e) {
           throw new MongoDaoException(e);
       }
    }

    public List<T> findAll(Class<T> clazz)  throws MongoDaoException {
        try {
            return mongoOperations.findAll(clazz);
        } catch (Exception e) {
            throw new MongoDaoException(e);
        }
    }

    public void delteByIds(String[] idNames, Object[] values, Class clazz) throws MongoDaoException {
        try {
            if (null == idNames) {
                throw new MongoDaoException("Argument not illegal");
            }
            Query query = null;
            Criteria criteria = new Criteria();
            int index = 0;
            for (String idName : idNames) {
                criteria.and(idName).is(values[index]);
                index++;
            }
            query = Query.query(criteria);
            mongoOperations.remove(query, clazz);
        } catch (Exception e) {
            throw new MongoDaoException(e);
        }
    }

    public void delteById(String idName, Object value, Class clazz) throws MongoDaoException {
        try {
            Query query = null;
            Criteria criteria = new Criteria();
            criteria.where(idName).is(value);
            query = Query.query(criteria);
            mongoOperations.remove(query, clazz);
        } catch (Exception e) {
            throw new MongoDaoException(e);
        }
    }

    public void delete(T t) throws MongoDaoException {
        try {
            Query query = null;
            Criteria criteria = new Criteria();
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                ClassUtil.EntityFieldInfo entityFieldInfo = ClassUtil.getEntityFieldInfo(field);
                if (entityFieldInfo != null) {
                    if (entityFieldInfo.isId) {
                        criteria.and(entityFieldInfo.modelName).is(field.get(t));
                    }
                }
            }
            query = Query.query(criteria);
            mongoOperations.remove(query, t.getClass());
        } catch (Exception e) {
            throw new MongoDaoException(e);
        }
    }

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public static void main(String[] args) {
        new MongoBaseDao().init();
    }
}
