package com.at.amq.hc;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.util.InetAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import javax.inject.Provider;

/**
 * Created by eyonlig on 9/28/2017.
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.at.amq.hc")
@Component
public class HealthCheckSchedule {
    @Value("#{${hc.services}}")
    private Map<String, String> hcServices = new HashMap<>();
    @Autowired
    private Provider<HeartBeatAgent> agentProvider;
    private Map<Integer, HeartBeatAgent> agents = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        Iterator<Map.Entry<String, String>> iterator = hcServices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(String.format("%s = %s", entry.getKey(), entry.getValue()));
            if (RegexUtil.isServiceId(key)) {
                HeartBeatAgent hba = agentProvider.get();
                hba.setServiceId(value);
                agents.put(Integer.valueOf(RegexUtil.parseSequence(key)), hba);
            } else if (RegexUtil.isServicePort(key)) {
                HeartBeatAgent hba = agents.get(RegexUtil.parseSequence(key));
                hba.setServicePort(Integer.parseInt(value));
            }
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void process() {
        Iterator<Map.Entry<Integer, HeartBeatAgent>> iterator = agents.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, HeartBeatAgent> entry = iterator.next();
            executor.submit(entry.getValue());
        }
    }


    @Bean
    public static PropertyPlaceholderConfigurer properties() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ClassPathResource[] resources = new ClassPathResource[]
                {new ClassPathResource("hc.properties")};
        ppc.setLocations(resources);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(HealthCheckSchedule.class);
    }
}

class ServiceParam {
    public String serviceId;
    public String nodeId;
    public String subNodeId;
    public String serviceUrl;
}