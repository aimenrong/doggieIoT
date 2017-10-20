package com.at.amqrouter.service.impl;

import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.service.AliasResolverCacheService;
import com.at.amqrouter.service.AliasResolverService;
import com.at.amqrouter.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Service("BrokerUrlResolver")
public class BrokerUrlResolver implements AliasResolverService {
    @Resource(name = "SimpleAliasResolverCache")
    private AliasResolverCacheService aliasResolverCacheService;
    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    @Value("${registry.lookup.broker.service.url}")
    private String registryLookupBrokerServiceUrl;

    /**
     *
     * @param serviceUrl
     * @param args
     * @return
     */
    public Object resolve(String serviceUrl, String...args) {
        BrokerServiceComponent result = (BrokerServiceComponent) aliasResolverCacheService.lookup(serviceUrl, args);
        if (null == result) {
            String brokerId = args[0];
            BrokerServiceComponent brokerServiceComponent = lookupBrokerInfo(StringUtil.extractServiceId(serviceUrl), brokerId);
            aliasResolverCacheService.store(serviceUrl, brokerServiceComponent, args);
            return brokerServiceComponent;
        }
        return result;
    }

    private BrokerServiceComponent lookupBrokerInfo(String serviceId, String brokerId) {
        BrokerServiceComponent brokerServiceComponent = restTemplate.getForObject(String.format(this.registryLookupBrokerServiceUrl, serviceId, brokerId),
                BrokerServiceComponent.class);
        return brokerServiceComponent;
    }
}
