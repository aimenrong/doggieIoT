package com.at.registry.bean.registry;

import com.alibaba.fastjson.JSON;
import com.at.registry.dao.Entity;
import com.at.registry.dao.EntityField;
import com.at.registry.dao.EntityId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
@Entity("ServiceComponents")
public class ServiceComponent {
    @EntityId("serviceId")
    private String serviceId;

    @EntityField("serviceNodes")
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
