package com.at.amqrouter.bean;

import com.at.amqrouter.bean.registry.BrokerServiceComponent;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
public class DeviceInfo {
    private String deviceId;
    private String brokerId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }
}
