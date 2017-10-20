package com.at.registry.service;

import com.at.registry.bean.DeviceInfo;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
public interface DeviceRegistryService {
    DeviceInfo lookupDevice(String deviceId);

    void registryDevice(DeviceInfo deviceInfo);

    void unregistryDevice(String deviceId);
}
