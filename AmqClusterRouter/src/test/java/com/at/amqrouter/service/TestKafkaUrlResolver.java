package com.at.amqrouter.service;

import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.bean.registry.ServiceComponent;
import com.at.amqrouter.bean.registry.ServiceNode;
import com.at.amqrouter.bean.registry.ServiceSubNode;
import com.at.amqrouter.service.impl.BrokerUrlResolver;
import com.at.amqrouter.service.impl.KafkaUrlResolver;
import com.at.amqrouter.service.impl.SimpleAliasResolverCache;
import com.at.amqrouter.service.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
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
public class TestKafkaUrlResolver {
    @Mock
    RestTemplate restTemplate;
    AliasResolverCacheService aliasResolverCacheService;
    KafkaUrlResolver kafkaUrlResolver;
    ServiceComponent serviceComponent;

    @Before
    public void init() {
        kafkaUrlResolver = new KafkaUrlResolver();
        aliasResolverCacheService = new SimpleAliasResolverCache();
        ReflectionUtil.setPropertyByReflection(kafkaUrlResolver, "restTemplate", restTemplate);
        ReflectionUtil.setPropertyByReflection(kafkaUrlResolver, "aliasResolverCacheService", aliasResolverCacheService);
        ReflectionUtil.setPropertyByReflection(kafkaUrlResolver, "registryLookupKafkaServiceUrl", "http://fake.com/fake?serviceId=%s");
        serviceComponent = new ServiceComponent();
        ServiceSubNode serviceSubNode = new ServiceSubNode();
        serviceSubNode.setServiceUrl("a-PC:9092");
        ServiceNode serviceNode = new ServiceNode();
        serviceNode.getSubNodes().put("0", serviceSubNode);
        serviceComponent.getServiceNodes().put("0", serviceNode);
    }

    @Test
    public void testResolve() {
        when(restTemplate.getForObject(anyString(), Matchers.<Class<ServiceComponent>> any(), Matchers.<Object>anyVararg())).thenReturn(serviceComponent);
        when(restTemplate.getForObject(anyString(), Matchers.<Class<ServiceComponent>> any())).thenReturn(serviceComponent);
        ServiceComponent ret = (ServiceComponent) kafkaUrlResolver.resolve("KAFKA-SERVICE", "0");
        Assert.assertEquals(ret.getServiceNodes().get("0").getSubNodes().get("0").getServiceUrl(), "a-PC:9092");
    }

    @Test
    public void testResolveWithCache() {
        aliasResolverCacheService.store("KAFKA-SERVICE", serviceComponent);
        ServiceComponent ret = (ServiceComponent) kafkaUrlResolver.resolve("KAFKA-SERVICE");
        Assert.assertEquals(ret.getServiceNodes().get("0").getSubNodes().get("0").getServiceUrl(), serviceComponent.getServiceNodes().get("0").getSubNodes().get("0").getServiceUrl());
        verify(restTemplate, times(0)).getForObject(anyString(), Matchers.<Class<BrokerServiceComponent>> any());
    }
}
