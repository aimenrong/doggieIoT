package com.at.registry.dao;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.exception.DaoException;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
public interface SubscriptionRegistryDao {
    List<SubscriptionBean> lookupSubscriptionsByDeviceId(String deviceId) throws DaoException;

    List<SubscriptionBean> lookupExpiredSubscriptionsByDeviceId(String deviceId) throws DaoException;

    void addSubscription(SubscriptionBean subscriptionBean) throws DaoException;

    void updateSubscription(SubscriptionBean subscriptionBean) throws DaoException;

    void deleteSubscription(String subscriptionId) throws DaoException;
}
