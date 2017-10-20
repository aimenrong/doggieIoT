package com.at.registry.dao;

import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.exception.DaoException;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
public interface ServiceRegistryDao {
    void addService(ServiceComponent serviceComponent) throws DaoException;

    void updateService(ServiceComponent serviceComponent) throws DaoException;

    ServiceComponent getService(String serviceId) throws DaoException;

    void removeService(String serviceId) throws DaoException;

    List<ServiceComponent> getAllServices() throws DaoException;
}
