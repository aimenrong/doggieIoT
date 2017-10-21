package com.at.registry.service.impl;

import com.at.registry.bean.DeviceInfo;
import com.at.registry.bean.SubscriptionBean;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.SubscriptionRegistryService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Service("DummySubscriptionRegistryService")
public class DummySubscriptionRegistryService implements SubscriptionRegistryService {
    private Map<String, List<SubscriptionBean>> map = new ConcurrentHashMap<String, List<SubscriptionBean>>();

    public void updateSubscriptionNextTriggerTime(String subscriptionId) {

    }

    public void deleteSubscription(String subscriptionId) throws ServiceException {

    }

    public List<SubscriptionBean> lookupSubscriptionByDeviceId(String deviceId) {
        if (map.containsKey(deviceId)) {
            return map.get(deviceId);
        }
        List<SubscriptionBean> list = new LinkedList<SubscriptionBean>();
        SubscriptionBean subscriptionBean = new SubscriptionBean();
        subscriptionBean.setDeviceId(deviceId);
        subscriptionBean.setFunctionId("notificationFunction");
        subscriptionBean.setContent("Hello World");
        subscriptionBean.setLongitude(10.2);
        subscriptionBean.setLatitude(85.3);
        list.add(subscriptionBean);
        map.put(deviceId, list);
        return list;
    }

    public void addSubscription(SubscriptionBean subscriptionBean) throws ServiceException {

    }

    public void updateSubscription(SubscriptionBean subscriptionBean) throws ServiceException {

    }

    public void deleteSubscription(String deviceId, String functionId) throws ServiceException {

    }
}
