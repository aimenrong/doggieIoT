package com.at.registry.dao.impl;

import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.dao.ServiceRegistryDao;
import com.at.registry.exception.DaoException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
@Component("SimpleServiceRegistryDao")
public class SimpleServiceRegistryDao implements ServiceRegistryDao {
    private Map<String, ServiceComponent> serviceStore = new ConcurrentHashMap<String, ServiceComponent>();

    public void addService(ServiceComponent serviceComponent) {
        serviceStore.put(serviceComponent.getServiceId(), serviceComponent);
    }

    public void updateService(ServiceComponent serviceComponent) {
        serviceStore.put(serviceComponent.getServiceId(), serviceComponent);
    }

    public ServiceComponent getService(String serviceId) {
        return serviceStore.get(serviceId);
    }

    public void removeService(String serviceId) {
        if (serviceStore.containsKey(serviceId)) {
            serviceStore.remove(serviceId);
        }
    }

    public List<ServiceComponent> getAllServices() throws DaoException {
        return null;
    }
}
