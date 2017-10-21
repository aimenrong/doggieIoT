package com.at.amqrouter.schedule;

import com.alibaba.fastjson.JSON;
import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.bean.registry.ServiceComponent;
import com.at.amqrouter.bean.registry.ServiceNode;
import com.at.amqrouter.bean.registry.ServiceSubNode;
import com.at.amqrouter.service.BrokerConsumerService;
import com.at.amqrouter.service.ClusterBrokerService;
import com.at.amqrouter.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Configuration
@EnableScheduling
public class BrokerSchedule {
    @Resource(name = "ClusterBrokerServiceImpl")
    private ClusterBrokerService clusterBrokerService;

    @Resource(name = "JmsConsumerService")
    private BrokerConsumerService brokerConsumerService;

    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    @Value("${registry.lookup.service.url}")
    private String serviceLookupUrl;
    @Value("${openwire.broker.service.url}")
    private String openwireBrokerServiceUrl;
    @Value("${mqtt.broker.service.url}")
    private String mqttBrokerServiceUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerSchedule.class);

    @Scheduled(initialDelay = 10000, fixedDelay = 3000)
    public void consumeBrokerClusterMessage() {
        List<BrokerServiceComponent> list = fetchRegisteredBrokers(StringUtil.extractServiceId(this.openwireBrokerServiceUrl));
        if (list != null) {
            for (BrokerServiceComponent brokerServiceComponent : list) {
                brokerConsumerService.consumeMessage(brokerServiceComponent.getBrokerId());
            }
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 3000)
    public void maintainBroadcastConnection() {
        List<BrokerServiceComponent> list = fetchRegisteredBrokers(StringUtil.extractServiceId(this.mqttBrokerServiceUrl));
        if (list != null) {
            for (BrokerServiceComponent brokerServiceComponent : list) {
                clusterBrokerService.checkBroadcastConnection(brokerServiceComponent.getBrokerId());
            }
        }
    }

    private List<BrokerServiceComponent> fetchRegisteredBrokers(String serviceId) {
        ServiceComponent serviceComponent = restTemplate.getForObject(String.format(serviceLookupUrl, serviceId), ServiceComponent.class);
        List<BrokerServiceComponent> list = new ArrayList<BrokerServiceComponent>();
        try {
            if (serviceComponent != null) {
                Iterator<Map.Entry<String, ServiceNode>> iterator = serviceComponent.getServiceNodes().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ServiceNode> entry1 = iterator.next();
                    String brokerId = entry1.getKey();
                    BrokerServiceComponent brokerServiceComponent = BrokerServiceComponent.buildBrokerServiceComponent(serviceId, brokerId);
                    list.add(brokerServiceComponent);
                    Iterator<Map.Entry<String, ServiceSubNode>> iterator2 = entry1.getValue().getSubNodes().entrySet().iterator();
                    while (iterator2.hasNext()) {
                        Map.Entry<String, ServiceSubNode> entry2 = iterator2.next();
                        String subNodeId = entry2.getKey();
                        ServiceSubNode serviceSubNode = entry2.getValue();
                        brokerServiceComponent.getServiceUrls().put(subNodeId, serviceSubNode.getServiceUrl());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return list;
    }
}
