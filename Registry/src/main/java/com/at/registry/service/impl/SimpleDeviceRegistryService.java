package com.at.registry.service.impl;

import com.at.registry.bean.DeviceInfo;
import com.at.registry.dao.DeviceRegistryDao;
import com.at.registry.service.DeviceRegistryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Service("SimpleDeviceRegistryService")
public class SimpleDeviceRegistryService implements DeviceRegistryService {
    @Resource(name = "SimpleDeviceRegistryDao")
    private DeviceRegistryDao deviceRegistryDao;

    public DeviceInfo lookupDevice(String deviceId) {
        return deviceRegistryDao.lookupDevice(deviceId);
    }

    public void registryDevice(DeviceInfo deviceInfo) {
        deviceRegistryDao.registryDevice(deviceInfo);
    }

    public void unregistryDevice(String deviceId) {
        deviceRegistryDao.unregistryDevice(deviceId);
    }
}
