package com.at.registry.dao.impl;

import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.dao.MongoBaseDao;
import com.at.registry.dao.ServiceRegistryDao;
import com.at.registry.exception.DaoException;
import com.at.registry.exception.MongoDaoException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
@Component("ServiceRegistryMongoDao")
public class ServiceRegistryMongoDao extends MongoBaseDao<ServiceComponent> implements ServiceRegistryDao {
    public void addService(ServiceComponent serviceComponent) throws DaoException {
        try {
            super.insert(serviceComponent);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public void updateService(ServiceComponent serviceComponent) throws DaoException {
        try {
            super.update(serviceComponent, true);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public ServiceComponent getService(String serviceId) throws DaoException {
        try {
            return super.findOneByParam("serviceId", serviceId, ServiceComponent.class);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public void removeService(String serviceId) throws DaoException {
        try {
            super.delteById("serviceId", serviceId, ServiceComponent.class);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public List<ServiceComponent> getAllServices() throws DaoException {
        try {
            return super.findAll(ServiceComponent.class);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }
}
