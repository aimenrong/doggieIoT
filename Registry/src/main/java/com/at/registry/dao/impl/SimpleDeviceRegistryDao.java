package com.at.registry.dao.impl;

import com.at.registry.bean.DeviceInfo;
import com.at.registry.dao.DeviceRegistryDao;
import org.springframework.stereotype.Component;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Component("SimpleDeviceRegistryDao")
public class SimpleDeviceRegistryDao implements DeviceRegistryDao {
    private Map<String, DeviceInfo> map = new ConcurrentHashMap<String, DeviceInfo>();

    public DeviceInfo lookupDevice(String deviceId) {
        if (map.containsKey(deviceId)) {
            return map.get(deviceId);
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setBrokerId("0");
        registryDevice(deviceInfo);
        return deviceInfo;
    }

    public void registryDevice(DeviceInfo deviceInfo) {
        map.put(deviceInfo.getDeviceId(), deviceInfo);
    }

    public void unregistryDevice(String deviceId) {
        if (map.containsKey(deviceId)) {
            map.remove(deviceId);
        }
    }
}
