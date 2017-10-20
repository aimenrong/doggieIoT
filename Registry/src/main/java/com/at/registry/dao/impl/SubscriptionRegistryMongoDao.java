package com.at.registry.dao.impl;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.dao.MongoBaseDao;
import com.at.registry.dao.SubscriptionRegistryDao;
import com.at.registry.exception.DaoException;
import com.at.registry.exception.MongoDaoException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Terry LIANG on 2017/10/4.
 */
@Component("SubscriptionRegistryMongoDao")
public class SubscriptionRegistryMongoDao extends MongoBaseDao<SubscriptionBean> implements SubscriptionRegistryDao {
    public List<SubscriptionBean> lookupSubscriptionsByDeviceId(String deviceId) throws DaoException {
        try {
            List<SubscriptionBean> list = super.findByParam("deviceId", deviceId, SubscriptionBean.class);
            return list;
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public void addSubscription(SubscriptionBean subscriptionBean) throws DaoException {
        try {
            super.insert(subscriptionBean);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public void updateSubscription(SubscriptionBean subscriptionBean) throws DaoException {
        try {
            super.update(subscriptionBean, false);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public void deleteSubscription(String subscriptionId) throws DaoException {
        try {
            super.delteByIds(new String[]{"subscriptionId"}, new String[]{subscriptionId}, SubscriptionBean.class);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }
}
