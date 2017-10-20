package com.at.amqrouter.service.impl;

import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.bean.registry.ServiceComponent;
import com.at.amqrouter.service.AliasResolverCacheService;
import com.at.amqrouter.service.AliasResolverService;
import com.at.amqrouter.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Component("KafkaUrlResolver")
public class KafkaUrlResolver implements AliasResolverService {
    @Resource(name = "SimpleAliasResolverCache")
    private AliasResolverCacheService aliasResolverCacheService;
    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    @Value("${registry.lookup.service.url}")
    private String registryLookupKafkaServiceUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUrlResolver.class);

    /**
     *
     * @param input - KAFKA SERVICE ID
     * @param args
     * @return
     */
    public Object resolve(String input, String...args) {
        ServiceComponent result = (ServiceComponent) aliasResolverCacheService.lookup(input, args);
        if (null == result) {
            ServiceComponent serviceComponent = lookupKafkaBrokerInfo(input);
            if (serviceComponent != null) {
                aliasResolverCacheService.store(input, serviceComponent, args);
            }
            return serviceComponent;
        }
        return result;
    }

    private ServiceComponent lookupKafkaBrokerInfo(String serviceId) {
       try {
           ServiceComponent serviceComponent = restTemplate.getForObject(String.format(this.registryLookupKafkaServiceUrl, serviceId),
                   ServiceComponent.class);
           return serviceComponent;
       } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
       }
        return null;
    }
}
