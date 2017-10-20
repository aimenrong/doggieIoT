package com.at.registry.bean;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
public class ServiceParam {
    private String serviceId;
    private String nodeId;
    private String subNodeId;
    private String serviceUrl;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSubNodeId() {
        return subNodeId;
    }

    public void setSubNodeId(String subNodeId) {
        this.subNodeId = subNodeId;
    }

    public static ServiceParam buildServiceParam(String serviceId, String nodeId, String subNodeId, String serviceUrl) {
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setServiceId(serviceId);
        serviceParam.setNodeId(nodeId);
        serviceParam.setSubNodeId(subNodeId);
        serviceParam.setServiceUrl(serviceUrl);
        return serviceParam;
    }
}