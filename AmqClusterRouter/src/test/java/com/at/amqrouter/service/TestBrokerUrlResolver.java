package com.at.amqrouter.service;

import com.at.amqrouter.bean.DeviceInfo;
import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.service.impl.BrokerUrlResolver;
import com.at.amqrouter.service.impl.SimpleAliasResolverCache;
import com.at.amqrouter.service.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Terry LIANG on 2017/10/3.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBrokerUrlResolver {
    @Mock RestTemplate restTemplate;
    AliasResolverCacheService aliasResolverCacheService;
    BrokerUrlResolver brokerUrlResolver;
    BrokerServiceComponent brokerServiceComponent;

    @Before
    public void init() {
        brokerUrlResolver = new BrokerUrlResolver();
        aliasResolverCacheService = new SimpleAliasResolverCache();
        ReflectionUtil.setPropertyByReflection(brokerUrlResolver, "restTemplate", restTemplate);
        ReflectionUtil.setPropertyByReflection(brokerUrlResolver, "aliasResolverCacheService", aliasResolverCacheService);
        ReflectionUtil.setPropertyByReflection(brokerUrlResolver, "registryLookupBrokerServiceUrl", "http://fake.com/fake?serviceId=%s&brokerId=%s");
    }

    @Test
    public void testResolve() {
        brokerServiceComponent = new BrokerServiceComponent();
        brokerServiceComponent.setBrokerId("0");
        brokerServiceComponent.getServiceUrls().put("0", "tcp://a-PC:1883");
        when(restTemplate.getForObject(anyString(), Matchers.<Class<BrokerServiceComponent>> any(), Matchers.<Object>anyVararg())).thenReturn(brokerServiceComponent);
        when(restTemplate.getForObject(anyString(), Matchers.<Class<BrokerServiceComponent>> any())).thenReturn(brokerServiceComponent);
        BrokerServiceComponent ret = (BrokerServiceComponent) brokerUrlResolver.resolve("tcp://MQTT-BROKER-SERVICE", "0");
        Assert.assertEquals(ret.getServiceUrls().get("0"), "tcp://a-PC:1883");
    }

    @Test
    public void testResolveWithCache() {
        brokerServiceComponent = new BrokerServiceComponent();
        brokerServiceComponent.setBrokerId("0");
        brokerServiceComponent.getServiceUrls().put("0", "tcp://a-PC:61616");
        aliasResolverCacheService.store("tcp://OPENWIRE-BROKER-SERVICE", brokerServiceComponent, "0");
        BrokerServiceComponent ret = (BrokerServiceComponent) brokerUrlResolver.resolve("tcp://OPENWIRE-BROKER-SERVICE", "0");
        Assert.assertEquals(ret.getServiceUrls().get("0"), brokerServiceComponent.getServiceUrls().get("0"));
        verify(restTemplate, times(0)).getForObject(anyString(), Matchers.<Class<BrokerServiceComponent>> any());
    }
}
