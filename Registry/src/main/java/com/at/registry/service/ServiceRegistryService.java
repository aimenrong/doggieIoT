package com.at.registry.service;

import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.bean.ServiceParam;
import com.at.registry.exception.ServiceException;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
public interface ServiceRegistryService {
    void registerService(ServiceParam serviceParam) throws ServiceException;

    void unregisterService(ServiceParam serviceParam) throws ServiceException;

    ServiceComponent getService(String serviceId) throws ServiceException;

    void updateHeartBeat(String serviceId, String nodeId, String subNodeId) throws ServiceException;

    List<ServiceComponent> getAllServices() throws ServiceException;
}
