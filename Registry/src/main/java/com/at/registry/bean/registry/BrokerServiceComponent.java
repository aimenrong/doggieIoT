package com.at.registry.bean.registry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
public class BrokerServiceComponent {
    // nodeId
    private String brokerId;
    private String serviceId;
    private Map<String, String> serviceUrls = new HashMap<String, String>();

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public Map<String, String> getServiceUrls() {
        return serviceUrls;
    }

    public static BrokerServiceComponent buildBrokerServiceComponent(String serviceId, String brokerId) {
        BrokerServiceComponent brokerServiceComponent = new BrokerServiceComponent();
        brokerServiceComponent.setServiceId(serviceId);
        brokerServiceComponent.setBrokerId(brokerId);
        return brokerServiceComponent;
    }
}
