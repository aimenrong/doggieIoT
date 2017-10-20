package com.at.registry.bean.registry;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
public class ServiceSubNode {
    private String nodeId;

    private String serviceUrl;

    private Map<String, Object> meta = new ConcurrentHashMap<String, Object>();

    private Date createdTime;

    private Date lastModifiedTime;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public static ServiceSubNode build(String subNodeId, String serviceUrl) {
        ServiceSubNode serviceSubNode = new ServiceSubNode();
        serviceSubNode.setNodeId(subNodeId);
        serviceSubNode.setServiceUrl(serviceUrl);
        serviceSubNode.setCreatedTime(new Date());
        serviceSubNode.setLastModifiedTime(new Date());
        return serviceSubNode;
    }
}
