package com.at.kafka.hc;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Matchers.eq;

/**
 * Created by eyonlig on 9/29/2017.
 */
@RunWith(PowerMockRunner.class)
//@PrepareForTest({HeartBeatMessage.class})
public class TestKafkaHealthCheck {
    private KafkaHealthCheck kafkaHealthCheck;
    @Mock private KafkaHCAgent mockAgent;

    @Mock private RestTemplate mockRestTemplate;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        kafkaHealthCheck = new KafkaHealthCheck();
    }

    @Test
    public void testHC() {
        try {
            setPropertyByReflection(kafkaHealthCheck, "kafkaHCAgent", mockAgent);
            setPropertyByReflection(kafkaHealthCheck, "registryServiceUrl", "http://hc.com");
            setPropertyByReflection(kafkaHealthCheck, "restTemplate", mockRestTemplate);
            setPropertyByReflection(kafkaHealthCheck, "registryHeartBeatUrl", "http://hb.com/%s:%s:%s");
            setPropertyByReflection(kafkaHealthCheck, "serviceId", "service1");
            setPropertyByReflection(kafkaHealthCheck, "nodeId", "0");
            setPropertyByReflection(kafkaHealthCheck, "subNodeId", "0");
            Map<String, String> brokerList = new HashMap<>();
            brokerList.put("0", "a-PC:9090");
            Mockito.when(mockAgent.getBootstrapList()).thenReturn(brokerList);
            GeneralResponse generalResponse = new GeneralResponse();
            generalResponse.setStatusCode(200);
            generalResponse.setMessage("success");
            Mockito.when(mockRestTemplate.postForObject(Mockito.anyString(), Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(JSON.toJSONString(generalResponse));
            Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON.toJSONString(generalResponse));
            kafkaHealthCheck.process();
            Assert.assertEquals(true, kafkaHealthCheck.getIsRegister().get());
            kafkaHealthCheck.process();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }
    }

    private void setPropertyByReflection(Object object, String key, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(key);
        field.setAccessible(true);
        field.set(object, value);
    }

    private Object getPropertyByReflection(Object object, String key) throws Exception {
        Field field = object.getClass().getDeclaredField(key);
        field.setAccessible(true);
        return field.get(object);
    }
}
