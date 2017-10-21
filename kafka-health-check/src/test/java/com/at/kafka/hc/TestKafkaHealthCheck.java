package com.at.kafka.hc;

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
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by eyonlig on 9/29/2017.
 */
@RunWith(PowerMockRunner.class)
//@PrepareForTest({HeartBeatMessage.class})
public class TestKafkaHealthCheck {
    private KafkaHealthCheck kafkaHealthCheck;
    @Mock
    private KafkaHCAgent mockAgent;

    @Mock
    private RestTemplate mockRestTemplate;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        kafkaHealthCheck = new KafkaHealthCheck();
        setPropertyByReflection(kafkaHealthCheck, "kafkaHCAgent", mockAgent);
        setPropertyByReflection(kafkaHealthCheck, "isRegister", new AtomicBoolean(true));
        setPropertyByReflection(kafkaHealthCheck, "restTemplate", mockRestTemplate);
    }

    public void testHC() {
        try {
            Map<String, String> brokerList = new HashMap<>();
            brokerList.put("0", "a-PC:9090");
            Mockito.when(mockAgent.getBootstrapList()).thenReturn(brokerList);
            Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn("success");
            kafkaHealthCheck.process();
            Mockito.verify(mockAgent, Mockito.times(1)).getBootstrapList();
        } catch (Exception e) {
            e.printStackTrace();
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
