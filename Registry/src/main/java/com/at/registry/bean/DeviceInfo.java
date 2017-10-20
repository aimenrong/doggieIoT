package com.at.registry.bean;

import com.at.registry.dao.Entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Entity("devices")
@XmlRootElement
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
