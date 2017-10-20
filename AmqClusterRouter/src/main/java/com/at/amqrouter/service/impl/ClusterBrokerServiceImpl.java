package com.at.amqrouter.service.impl;

import com.at.amqrouter.RestfulServer;
import com.at.amqrouter.bean.DeviceInfo;
import com.at.amqrouter.service.ClusterBrokerService;
import com.at.amqrouter.service.broker.BrokerContainer;
import com.at.amqrouter.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Terry LIANG on 2017/10/2.
 */
@Service("ClusterBrokerServiceImpl")
public class ClusterBrokerServiceImpl implements ClusterBrokerService {
    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    private Map<String, BrokerContainer> clusterBrokerContainer = new ConcurrentHashMap<String, BrokerContainer>();
    private Map<String, BrokerContainer> broadcastClusterBrokerContainer = new ConcurrentHashMap<String, BrokerContainer>();
    private Map<String, String> deviceIdCache = new ConcurrentHashMap<String, String>();
    @Value("${openwire.broker.service.url}")
    private String openwireBrokerServiceUrl;
    @Value("${mqtt.broker.service.url}")
    private String mqttBrokerServiceUrl;
    @Value("${device.registry.service.url}")
    private String deviceRegistryServiceUrl;
    @Value("${registry.lookup.broker.service.url}")
    private String registryLookupBrokerServiceUrl;
    private String openwireServiceId, mqttServiceId;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterBrokerServiceImpl.class);
    private Map<String, ReentrantLock> lockStore = new ConcurrentHashMap<String, ReentrantLock>();

    @PostConstruct
    public void init() {
        openwireServiceId = StringUtil.extractServiceId(this.openwireBrokerServiceUrl);
        mqttServiceId = StringUtil.extractServiceId(this.mqttBrokerServiceUrl);
    }

    public Connection getDeviceConnection(String deviceId) {
        String brokerId = getBrokerOfDevice(deviceId);
        if (brokerId != null) {
            return getQueueConnection(brokerId);
        }
        return null;
    }

    public Connection getQueueConnection(String brokerId) {
        String newKey = String.format("%s:%s", openwireServiceId, brokerId);
        if (clusterBrokerContainer.containsKey(newKey)) {
            return clusterBrokerContainer.get(newKey).getConnection();
        }
        BrokerContainer brokerContainer = (BrokerContainer) RestfulServer.getContext().getBean("BrokerContainer");
        brokerContainer.setBrokerInfo(brokerId, this.openwireBrokerServiceUrl);
        clusterBrokerContainer.put(newKey, brokerContainer);
        return brokerContainer.getConnection();
    }

    /**
     * VirtualTopic ( Queue ) listener
     * @param brokerId
     * @param messageConsumer
     */
    public void addQueueConsumer(String brokerId, MessageConsumer messageConsumer) {
        String newKey = String.format("%s:%s", this.openwireServiceId, brokerId);
        ReentrantLock lock = acquireLock(newKey);
        try {
            lock.lock();
            if (clusterBrokerContainer.containsKey(newKey)) {
                clusterBrokerContainer.get(newKey).setMessageConsumer(messageConsumer);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public boolean queueConsumerExist(String brokerId) {
        String newKey = String.format("%s:%s", this.openwireServiceId, brokerId);
        ReentrantLock lock = acquireLock(newKey);
        try {
            lock.lock();
            if (!clusterBrokerContainer.containsKey(newKey)) {
                return false;
            }
            BrokerContainer brokerContainer = this.clusterBrokerContainer.get(newKey);
            if (null == brokerContainer.getMessageConsumer()) {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public void removeQueueConsumer(String brokerId) {
        String newKey = String.format("%s:%s", this.openwireServiceId, brokerId);
        ReentrantLock lock = acquireLock(newKey);
        try {
            lock.lock();
            if (clusterBrokerContainer.containsKey(newKey)) {
                clusterBrokerContainer.get(newKey).getMessageConsumer().close();
                clusterBrokerContainer.get(newKey).setMessageConsumer(null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public boolean checkBroadcastConnection(String brokerId) {
        String newKey = String.format("%s:%s", mqttServiceId, brokerId);
        if (broadcastClusterBrokerContainer.containsKey(newKey)) {
            return true;
        }
        BrokerContainer brokerContainer = (BrokerContainer) RestfulServer.getContext().getBean("BrokerContainer");
        brokerContainer.setBrokerInfo(brokerId, this.mqttBrokerServiceUrl);
        broadcastClusterBrokerContainer.put(newKey, brokerContainer);
        return false;
    }

    public List<Connection> getBroadcastConnections() {
        List<Connection> list = new LinkedList<Connection>();
        Iterator<Map.Entry<String, BrokerContainer>> it = broadcastClusterBrokerContainer.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BrokerContainer> entry = it.next();
            list.add(entry.getValue().getConnection());
        }
        return list;
    }

    private String getBrokerOfDevice(String deviceId) {
        if (deviceIdCache.containsKey(deviceId)) {
            return deviceIdCache.get(deviceId);
        }
        // Get broker id of specific device from Registry
        DeviceInfo deviceInfo = lookupDeviceInfo(deviceId);
        if (deviceInfo != null) {
            deviceIdCache.put(deviceId, deviceInfo.getBrokerId());
            return deviceIdCache.get(deviceId);
        }
        return null;
    }

    private DeviceInfo lookupDeviceInfo(String deviceId) {
        String url = String.format(deviceRegistryServiceUrl, deviceId);
        DeviceInfo deviceInfo = restTemplate.getForObject(url, DeviceInfo.class);
        return deviceInfo;
    }

    private synchronized ReentrantLock acquireLock(String key) {
        ReentrantLock lock = lockStore.get(key);
        if (null == lock) {
            lock = new ReentrantLock();
            lockStore.put(key, lock);
        }
        return lock;
    }

    public Map<String, BrokerContainer> getBroadcastClusterBrokerContainer() {
        return broadcastClusterBrokerContainer;
    }

    public Map<String, BrokerContainer> getClusterBrokerContainer() {
        return clusterBrokerContainer;
    }

    public void setClusterBrokerContainer(Map<String, BrokerContainer> clusterBrokerContainer) {
        this.clusterBrokerContainer = clusterBrokerContainer;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setBroadcastClusterBrokerContainer(Map<String, BrokerContainer> broadcastClusterBrokerContainer) {
        this.broadcastClusterBrokerContainer = broadcastClusterBrokerContainer;
    }
}
