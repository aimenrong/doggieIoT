package com.at.registry.bean;

import com.at.registry.dao.Entity;
import com.at.registry.dao.EntityField;
import com.at.registry.dao.EntityId;

import java.util.Date;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Entity("subscriptions")
public class SubscriptionBean {
    @EntityId("subscriptionId")
    private String subscriptionId;
    @EntityField("deviceId")
    private String deviceId;
    @EntityField("functionId")
    private String functionId;
    @EntityField("longitude")
    private double longitude;
    @EntityField("latitude")
    private double latitude;
    @EntityField("content")
    private String content;
    @EntityField("createdTime")
    private Date createdTime;
    @EntityField("nextTriggerTime")
    private Date nextTriggerTime;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getNextTriggerTime() {
        return nextTriggerTime;
    }

    public void setNextTriggerTime(Date nextTriggerTime) {
        this.nextTriggerTime = nextTriggerTime;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
