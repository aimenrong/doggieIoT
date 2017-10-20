package com.at.registry.service.impl;

import com.at.registry.bean.*;
import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.bean.registry.ServiceNode;
import com.at.registry.bean.registry.ServiceSubNode;
import com.at.registry.dao.ServiceRegistryDao;
import com.at.registry.exception.DaoException;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.ServiceRegistryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
@Service("SimpleServiceRegistryService")
public class SimpleServiceRegistryService implements ServiceRegistryService {
    @Resource(name = "ServiceRegistryMongoDao")
    private ServiceRegistryDao serviceRegistryDao;
    private Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();

    public void registerService(ServiceParam serviceParam) throws ServiceException {
        String serviceId = serviceParam.getServiceId();
        ReentrantLock reentrantLock = acquireLock(serviceId);
        try {
            reentrantLock.lock();
            String nodeId = serviceParam.getNodeId();
            ServiceComponent serviceComponent = serviceRegistryDao.getService(serviceId);
            if (null == serviceComponent) {
                // service not exist
                serviceComponent = new ServiceComponent();
                serviceComponent.setServiceId(serviceId);
            }
            ServiceNode serviceNode = serviceComponent.getServiceNodes().get(nodeId);
            if (null == serviceNode) {
                // node not exist in this service
                serviceNode = new ServiceNode();
                serviceNode.setNodeId(nodeId);
                serviceComponent.getServiceNodes().put(nodeId, serviceNode);
            }
            String subNodeId = serviceParam.getSubNodeId();
            ServiceSubNode serviceSubNode = serviceNode.getSubNodes().get(subNodeId);
            if (null == serviceSubNode) {
                serviceSubNode = ServiceSubNode.build(subNodeId, serviceParam.getServiceUrl());
                serviceNode.getSubNodes().put(subNodeId, serviceSubNode);
            }
            serviceSubNode.setServiceUrl(serviceParam.getServiceUrl());
            serviceSubNode.setCreatedTime(new Date());
            serviceSubNode.setLastModifiedTime(new Date());
            serviceRegistryDao.updateService(serviceComponent);
        } catch (DaoException e) {
            throw new ServiceException(e);
        } finally {
            reentrantLock.unlock();
        }
    }

    private ReentrantLock acquireLock(String serviceId) {
        if (!lockMap.containsKey(serviceId)) {
            ReentrantLock reentrantLock = new ReentrantLock();
            lockMap.put(serviceId, reentrantLock);
        }
        return lockMap.get(serviceId);
    }

    public void unregisterService(ServiceParam serviceParam) throws ServiceException {
        String serviceId = serviceParam.getServiceId();
        ReentrantLock reentrantLock = acquireLock(serviceId);
        try {
            reentrantLock.lock();
            String nodeId = serviceParam.getNodeId();
            ServiceComponent serviceComponent = getService(serviceId);
            if (serviceComponent != null && serviceComponent.getServiceNodes().containsKey(nodeId)) {
                String subNodeId = serviceParam.getSubNodeId();
                if (serviceComponent.getServiceNodes().get(nodeId).getSubNodes().containsKey(subNodeId)) {
                    serviceComponent.getServiceNodes().get(nodeId).getSubNodes().remove(subNodeId);
                }
                serviceRegistryDao.updateService(serviceComponent);
            }
        } catch (DaoException e) {
            throw new ServiceException(e);
        } finally {
            reentrantLock.unlock();
        }
    }

    public ServiceComponent getService(String serviceId) throws ServiceException {
        try {
            return serviceRegistryDao.getService(serviceId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void updateHeartBeat(String serviceId, String nodeId, String subNodeId) throws ServiceException {
        try {
            boolean exception = false;
            ServiceComponent serviceComponent = getService(serviceId);
            if (serviceComponent != null) {
                ServiceNode serviceNode = serviceComponent.getServiceNodes().get(nodeId);
                if (serviceNode != null) {
                    ServiceSubNode serviceSubNode = serviceNode.getSubNodes().get(subNodeId);
                    if (serviceSubNode != null) {
                        serviceSubNode.setLastModifiedTime(new Date());
                        serviceRegistryDao.updateService(serviceComponent);
                    } else {
                        exception = true;
                    }
                } else {
                    exception = true;
                }
            } else {
                exception = true;
            }
            if (exception) {
                throw new ServiceException(String.format("Service %s not register yet", serviceId));
            }
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<ServiceComponent> getAllServices() throws ServiceException {
        try {
            return serviceRegistryDao.getAllServices();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
