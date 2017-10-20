package com.at.registry.schedule;

import com.at.registry.bean.*;
import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.bean.registry.ServiceNode;
import com.at.registry.bean.registry.ServiceSubNode;
import com.at.registry.service.ServiceRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
@Configuration
@EnableScheduling
public class ServiceHealthCheckScheduler {
    @Resource(name = "SimpleServiceRegistryService")
    private ServiceRegistryService serviceRegistryService;
    @Value("${registry.service.unhealth.interval.second:2}")
    private int unhealthInterval;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHealthCheckScheduler.class);

//    @Scheduled(fixedDelay = 2000)
    public void checkServiceHeatBeat() {
        try {
            List<ServiceComponent> serviceComponents = serviceRegistryService.getAllServices();
            for (ServiceComponent serviceComponent : serviceComponents) {
                Map<String, ServiceNode> serviceMeta = serviceComponent.getServiceNodes();
                Iterator<Map.Entry<String, ServiceNode>> iterator = serviceMeta.entrySet().iterator();
                while (iterator.hasNext()) {
                    ServiceNode serviceNode = iterator.next().getValue();
                    Iterator<Map.Entry<String, ServiceSubNode>> iterator2 = serviceNode.getSubNodes().entrySet().iterator();
                    while (iterator2.hasNext()) {
                        ServiceSubNode serviceSubNode = iterator2.next().getValue();
                        if ((System.currentTimeMillis() - serviceSubNode.getLastModifiedTime().getTime()) / 1000 > unhealthInterval) {
                            LOGGER.info("No heartbeat received, unregister service, serviceId : {}, nodeId : {}, subNodeId : {} serviceUrl : {}", serviceComponent.getServiceId(),
                                    serviceNode.getNodeId(), serviceSubNode.getNodeId(), serviceSubNode.getServiceUrl());
                            serviceRegistryService.unregisterService(ServiceParam.buildServiceParam(serviceComponent.getServiceId(), serviceNode.getNodeId(),
                                    serviceSubNode.getNodeId(), null));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
