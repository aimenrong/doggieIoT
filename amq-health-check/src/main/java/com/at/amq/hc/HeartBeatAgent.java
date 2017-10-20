package com.at.amq.hc;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.util.InetAddressUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
@Scope("prototype")
@Component("HeartBeatAgent")
public class HeartBeatAgent implements Runnable {
    @Resource(name = "HCRestTemplate")
    private RestTemplate restTemplate;
    private AtomicBoolean isRegister = new AtomicBoolean(false);

    @Value("${hc.test.mode.enabled:false}")
    private boolean testMode;
    @Value("${hc.registry.hb.url:http://localhost}")
    private String registryHeartBeatUrl;
    @Value("${hc.registry.service.url:http://localhost}")
    private String registerServiceUrl;

    @Value("${hc.host.node.id}")
    private String hostNodeId;
    @Value("${hc.host.subnode.id}")
    private String hostSubNodeId;
    private String host;
    private String portocol = "tcp";
    private String serviceId;
    private int servicePort;

    @PostConstruct
    public void init() {
        try {
            host = Inet4Address.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if (!testMode) {
            if (isRegister.get()) {
                sendHeartBeat();
            } else {
                register();
            }
        }
    }

    private void register() {
        try {
            ServiceParam serviceParam = new ServiceParam();
            serviceParam.serviceId = serviceId;
            serviceParam.nodeId = hostNodeId;
            serviceParam.subNodeId = hostSubNodeId;
            serviceParam.serviceUrl = String.format("%s://%s:%s", portocol, host, servicePort);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(serviceParam), headers);
            String string = restTemplate.postForObject(registerServiceUrl, formEntity, String.class);
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
                    this.hostNodeId, this.hostSubNodeId), String.class);
            GeneralResponse generalResponse = JSON.parseObject(response, GeneralResponse.class);
            if (generalResponse.getStatusCode() != 200) {
                isRegister.set(false);
            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPortocol() {
        return portocol;
    }

    public void setPortocol(String portocol) {
        this.portocol = portocol;
    }
}
