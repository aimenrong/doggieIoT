package com.at.amqrouter.service;

import com.at.amqrouter.aspect.AspectManager;
import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.bean.registry.ServiceComponent;
import com.at.amqrouter.bean.registry.ServiceNode;
import com.at.amqrouter.bean.registry.ServiceSubNode;
import com.at.amqrouter.service.util.ReflectionUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.org.eclipse.jdt.internal.compiler.codegen.ObjectCache;
import org.aspectj.runtime.internal.AroundClosure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.OPTIONS;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Terry LIANG on 2017/10/3.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAspectManager {
    TestProceedingJoinPoint proceedingJoinPoint;
    @Mock AliasResolverService aliasResolverService;
    @Mock AliasResolverService kafkaAliasResolverService;
    AspectManager aspectManager;
    BrokerServiceComponent openwireBrokerServiceComponent;
    BrokerServiceComponent mqttBrokerServiceComponent;
    ServiceComponent kafkaServiceComponent;

    @Before
    public void init() {
        aspectManager = new AspectManager();
        ReflectionUtil.setPropertyByReflection(aspectManager, "openwireBrokerServiceUrl", "tcp://OPENWIRE-BROKER-SERVICE");
        ReflectionUtil.setPropertyByReflection(aspectManager, "mqttBrokerServiceUrl", "tcp://MQTT-BROKER-SERVICE");
        openwireBrokerServiceComponent = new BrokerServiceComponent();
        openwireBrokerServiceComponent.setBrokerId("0");
        openwireBrokerServiceComponent.getServiceUrls().put("0", "tcp://a-PC:61616");
        mqttBrokerServiceComponent = new BrokerServiceComponent();
        mqttBrokerServiceComponent.setBrokerId("0");
        mqttBrokerServiceComponent.getServiceUrls().put("0", "tcp://a-PC:1883");
        proceedingJoinPoint = new TestProceedingJoinPoint();
        kafkaServiceComponent = new ServiceComponent();
        ServiceNode serviceNode = new ServiceNode();
        ServiceSubNode serviceSubNode = new ServiceSubNode();
        serviceSubNode.setNodeId("0");
        serviceSubNode.setServiceUrl("a-PC:9092");
        serviceNode.getSubNodes().put("0", serviceSubNode);
        kafkaServiceComponent.getServiceNodes().put("0", serviceNode);
    }

    @Test
    public void testReplaceBrokerUrl() {
        String[] args1 = new String[]{"0", "tcp://OPENWIRE-BROKER-SERVICE"};
        when(aliasResolverService.resolve(eq("tcp://OPENWIRE-BROKER-SERVICE"), anyString())).thenReturn(openwireBrokerServiceComponent);
        ReflectionUtil.setPropertyByReflection(aspectManager, "aliasResolverService", aliasResolverService);
        proceedingJoinPoint.args = args1;
        aspectManager.replaceBrokerUrl(proceedingJoinPoint);
        Assert.assertEquals("tcp://a-PC:61616", proceedingJoinPoint.getArgs()[1]);
        //
        args1 = new String[]{"0", "tcp://MQTT-BROKER-SERVICE"};
        when(aliasResolverService.resolve(eq("tcp://MQTT-BROKER-SERVICE"), anyString())).thenReturn(mqttBrokerServiceComponent);
        ReflectionUtil.setPropertyByReflection(aspectManager, "aliasResolverService", aliasResolverService);
        proceedingJoinPoint.args = args1;
        aspectManager.replaceBrokerUrl(proceedingJoinPoint);
        Assert.assertEquals("tcp://a-PC:1883", proceedingJoinPoint.getArgs()[1]);
    }

    @Test
    public void testReplaceKafkaUrl() {
        String[] args1 = new String[]{"bootstrap.servers", "KAFKA-SERVICE"};
        when(kafkaAliasResolverService.resolve(eq("KAFKA-SERVICE"))).thenReturn(kafkaServiceComponent);
        ReflectionUtil.setPropertyByReflection(aspectManager, "kafkaAliasResolverService", kafkaAliasResolverService);
        proceedingJoinPoint.args = args1;
        aspectManager.replaceKafkaUrl(proceedingJoinPoint);
        Assert.assertEquals("a-PC:9092", proceedingJoinPoint.getArgs()[1]);
        //
        args1 = new String[]{"test.prop", "Hello"};
        proceedingJoinPoint.args = args1;
        aspectManager.replaceKafkaUrl(proceedingJoinPoint);
        Assert.assertEquals("Hello", proceedingJoinPoint.getArgs()[1]);
    }
}

class TestProceedingJoinPoint implements ProceedingJoinPoint {
    public Object[] args;
    public void set$AroundClosure(AroundClosure aroundClosure) {

    }

    public Object proceed() throws Throwable {
        return null;
    }

    public Object proceed(Object[] objects) throws Throwable {
        args = objects;
        return null;
    }

    public String toShortString() {
        return null;
    }

    public String toLongString() {
        return null;
    }

    public Object getThis() {
        return null;
    }

    public Object getTarget() {
        return null;
    }

    public Object[] getArgs() {
        return args;
    }

    public Signature getSignature() {
        return null;
    }

    public SourceLocation getSourceLocation() {
        return null;
    }

    public String getKind() {
        return null;
    }

    public StaticPart getStaticPart() {
        return null;
    }
}
