package com.at.amqrouter.bean.registry;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public class ServiceComponent {
    private String serviceId;

    private Map<String, ServiceNode> serviceNodes = new ConcurrentHashMap<String, ServiceNode>();

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, ServiceNode> getServiceNodes() {
        return serviceNodes;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
