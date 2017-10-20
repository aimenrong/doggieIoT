package com.at.registry.service;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.exception.ServiceException;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
public interface SubscriptionRegistryService {
    List<SubscriptionBean> lookupSubscriptionByDeviceId(String deviceId) throws ServiceException;

    void addSubscription(SubscriptionBean subscriptionBean) throws ServiceException;

    void updateSubscription(SubscriptionBean subscriptionBean) throws ServiceException;

    void updateSubscriptionNextTriggerTime(String subscriptionId);

    void deleteSubscription(String subscriptionId) throws ServiceException;
}
