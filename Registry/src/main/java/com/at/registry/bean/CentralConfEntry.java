package com.at.registry.bean;

import com.at.registry.dao.Entity;
import com.at.registry.dao.EntityField;
import com.at.registry.dao.EntityId;

import java.util.Date;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
@Entity("central_conf")
public class CentralConfEntry {
    @EntityId("key")
    private String key;
    @EntityField("value")
    private Object value;
    @EntityField("description")
    private String description;
    @EntityField("createdTime")
    private Date createdTime;
    @EntityField("lastModifiedTime")
    private Date lastModifiedTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
