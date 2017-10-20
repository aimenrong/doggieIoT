package com.at.amqrouter.service;

import com.alibaba.fastjson.JSON;
import com.at.amqrouter.bean.DeviceEvent;
import com.at.amqrouter.service.impl.JmsConsumerService;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.*;
import java.lang.reflect.Field;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by eyonlig on 9/29/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestJmsConsumerService {
    private JmsConsumerService jmsConsumerService;
    @Mock ClusterBrokerService mockClusterBrokerService;
    @Mock Connection mockConnection;
    @Mock Session mockSession;
    @Mock Queue mockDestination;
    @Mock MessageConsumer mockMessageConsumer;
    @Mock SpcService<DeviceEvent> mockSpcService;

    @Before
    public void init() throws Exception {
        jmsConsumerService = new JmsConsumerService();
        setPropertyByReflection(jmsConsumerService, "clusterBrokerService", mockClusterBrokerService);
        setPropertyByReflection(jmsConsumerService, "spcService", mockSpcService);
        when(mockClusterBrokerService.getQueueConnection("0")).thenReturn(mockConnection);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createQueue(null)).thenReturn(mockDestination);
        when(mockSession.createConsumer(mockDestination)).thenReturn(mockMessageConsumer);
    }

    @Test
    public void testConsumeMessage() {
        jmsConsumerService.consumeMessage("0");
        Mockito.verify(mockClusterBrokerService, Mockito.times(1)).addQueueConsumer("0", mockMessageConsumer);
    }

    @Test
    public void testOnMessage() {
        ActiveMQBytesMessage mockMessage = Mockito.mock(ActiveMQBytesMessage.class);
        ByteSequence mockContent = Mockito.mock(ByteSequence.class);
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setContent("hello world");
        deviceEvent.setDeviceId("device1");
        deviceEvent.setLatitude(10.2);
        when(mockMessage.getContent()).thenReturn(mockContent);
        when(mockContent.getData()).thenReturn(JSON.toJSONString(deviceEvent).getBytes());
        jmsConsumerService.onMessage(mockMessage);
        verify(mockSpcService, times(1)).sendMessage(Mockito.any(DeviceEvent.class));
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
