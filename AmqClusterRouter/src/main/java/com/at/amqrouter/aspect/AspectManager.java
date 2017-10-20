package com.at.amqrouter.aspect;

import com.at.amqrouter.bean.registry.BrokerServiceComponent;
import com.at.amqrouter.bean.registry.ServiceComponent;
import com.at.amqrouter.bean.registry.ServiceNode;
import com.at.amqrouter.bean.registry.ServiceSubNode;
import com.at.amqrouter.service.AliasResolverService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * Created by eyonlig on 9/25/2017.
 */
@Aspect
@Component("AspectManager")
public class AspectManager {
    @Value("${openwire.broker.service.url}")
    private String openwireBrokerServiceUrl;
    @Value("${mqtt.broker.service.url}")
    private String mqttBrokerServiceUrl;

    @Resource(name = "BrokerUrlResolver")
    private AliasResolverService aliasResolverService;

    @Resource(name = "KafkaUrlResolver")
    private AliasResolverService kafkaAliasResolverService;

    public AspectManager() {
        System.out.println("init AspectManager");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectManager.class);

    @Around("execution(* com.at.amqrouter.broker.BrokerContainer.setBrokerInfo(..))")
    public void replaceBrokerUrl(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        String brokerId = (String) args[0];
        String brokerUrl = (String) args[1];
        if (brokerUrl.contains(this.openwireBrokerServiceUrl)) {
            LOGGER.info("Replace Openwire broker URL");
            // Get the real and validated url from registry
            BrokerServiceComponent brokerServiceComponent = (BrokerServiceComponent) aliasResolverService.resolve(brokerUrl, brokerId);
            // hashing the subNodeId
            Collection<String> urls = brokerServiceComponent.getServiceUrls().values();
            for (String url : urls) {
                args[1] = url;
                break;
            }
        } else if (brokerUrl.contains(this.mqttBrokerServiceUrl)) {
            LOGGER.info("Replace MQTT broker URL");
            // Get the real and validated url from registry
            BrokerServiceComponent brokerServiceComponent = (BrokerServiceComponent) aliasResolverService.resolve(brokerUrl, brokerId);
            // hashing
            Collection<String> urls = brokerServiceComponent.getServiceUrls().values();
            for (String url : urls) {
                args[1] = url;
                break;
            }
        }
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Around("execution(* com.at.amqrouter.aspect.AspectProperties.put(..))")
    public void replaceKafkaUrl(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        String key = (String) args[0];
        if ("bootstrap.servers".equals(key)) {
            String kafkaUrl = (String) args[1];
            System.out.println("Aspect to replace the Kafka url");
            ServiceComponent serviceComponent = (ServiceComponent) kafkaAliasResolverService.resolve(kafkaUrl);
            // hashing it
            String subNodeId = "0";
            Iterator<Map.Entry<String, ServiceNode>> it = serviceComponent.getServiceNodes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ServiceNode> entry1 = it.next();
                Iterator<Map.Entry<String, ServiceSubNode>> it2 = entry1.getValue().getSubNodes().entrySet().iterator();
                while (it2.hasNext()) {
                    ServiceSubNode serviceSubNode = it2.next().getValue();
                    args[1] = serviceSubNode.getServiceUrl();
                }
            }
        }
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
