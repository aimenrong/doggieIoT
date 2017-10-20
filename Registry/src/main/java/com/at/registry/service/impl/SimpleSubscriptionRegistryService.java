package com.at.registry.service.impl;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.dao.SubscriptionRegistryDao;
import com.at.registry.exception.DaoException;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.SubscriptionRegistryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Service("SimpleSubscriptionRegistryService")
public class SimpleSubscriptionRegistryService implements SubscriptionRegistryService {
    @Resource(name = "SubscriptionRegistryMongoDao")
    private SubscriptionRegistryDao subscriptionRegistryDao;

    public List<SubscriptionBean> lookupSubscriptionByDeviceId(String deviceId) throws ServiceException {
        try {
            return subscriptionRegistryDao.lookupSubscriptionsByDeviceId(deviceId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void addSubscription(SubscriptionBean subscriptionBean) throws ServiceException {
        try {
            subscriptionRegistryDao.addSubscription(subscriptionBean);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void updateSubscription(SubscriptionBean subscriptionBean) throws ServiceException {
        try {
            subscriptionRegistryDao.updateSubscription(subscriptionBean);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void deleteSubscription(String subscriptionId) throws ServiceException {
        try {
            subscriptionRegistryDao.deleteSubscription(subscriptionId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void updateSubscriptionNextTriggerTime(String subscriptionId) {

    }
}
