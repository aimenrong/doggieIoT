package com.at.kafka.hc;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by eyonlig on 9/28/2017.
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"com.at.kafka.hc"})
@Component("KafkaHealthCheck")
public class KafkaHealthCheck {
    @Autowired
    private RestTemplate restTemplate;
    @Resource(name = "KafkaHCAgent")
    private KafkaHCAgent kafkaHCAgent;
    @Value("${hc.service.id:KAFKA-SERVICE}")
    private String serviceId;
    @Value("${hc.host.node.id}")
    private String nodeId;
    @Value("${hc.host.subnode.id}")
    private String subNodeId;
    @Value("${hc.test.mode.enabled:false}")
    private boolean testMode;
    @Value("${hc.registry.hb.url}")
    private String registryHeartBeatUrl;
    @Value("${hc.registry.service.url}")
    private String registryServiceUrl;
    @Value("${hc.interval:5000}")
    private int hcInterval;

    private AtomicBoolean isRegister = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 3000)
    public void process() throws Exception {
        if (kafkaHCAgent != null) {
            Map<String, String> brokerList = kafkaHCAgent.getBootstrapList();
            Iterator<Entry<String, String>> it = brokerList.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                if (!testMode) {
                    if (isRegister.get()) {
                        sendHeartBeat();
                    } else {
                        register(entry.getValue());
                    }
                }
            }
        } else {
            System.err.println("KafkaHCAgent is not ready yet...");
        }
    }

    private void register(String serviceUrl) {
        try {
            ServiceParam serviceParam = new ServiceParam();
            serviceParam.serviceId = serviceId;
            serviceParam.nodeId = nodeId;
            serviceParam.subNodeId = subNodeId;
            serviceParam.serviceUrl = serviceUrl;
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(serviceParam), headers);
            String string = restTemplate.postForObject(registryServiceUrl, formEntity, String.class);
            GeneralResponse generalResponse = JSON.parseObject(string, GeneralResponse.class);
            if ("success".equals(generalResponse.getMessage())) {
                isRegister.set(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHeartBeat() {
        try {
            String response = restTemplate.getForObject(String.format(this.registryHeartBeatUrl, this.serviceId,
                    this.nodeId, this.subNodeId), String.class);
            GeneralResponse generalResponse = JSON.parseObject(response, GeneralResponse.class);
            if (generalResponse.getStatusCode() != 200) {
                isRegister.set(false);
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public static PropertyPlaceholderConfigurer ppc() throws IOException {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setLocations(new FileSystemResource(System.getProperty("hc.property.file")));
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(KafkaHealthCheck.class);
        applicationContext.refresh();
        while (true) {
            Thread.sleep(5000);
        }
    }
}

class ServiceParam {
    public String serviceId;
    public String nodeId;
    public String subNodeId;
    public String serviceUrl;
}
