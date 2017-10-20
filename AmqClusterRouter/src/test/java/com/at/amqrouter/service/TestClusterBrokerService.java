package com.at.amqrouter.service;

import com.at.amqrouter.RestfulServer;
import com.at.amqrouter.bean.DeviceInfo;
import com.at.amqrouter.service.broker.BrokerContainer;
import com.at.amqrouter.service.impl.ClusterBrokerServiceImpl;
import com.at.amqrouter.service.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

/**
 * Created by Terry LIANG on 2017/10/2.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RestfulServer.class})
public class TestClusterBrokerService {
    @Mock ApplicationContext applicationContext;
    @Mock RestTemplate restTemplate;
    @Mock BrokerContainer brokerContainer;
    @Mock Connection connection;
    ClusterBrokerServiceImpl clusterBrokerService;
    @Mock Map<String, BrokerContainer> clusterBrokerContainer;
    @Mock MessageConsumer messageConsumer;
    @Mock Map<String, BrokerContainer> broadcastClusterBrokerContainer;

    @Before
    public void init() {
        clusterBrokerService = new ClusterBrokerServiceImpl();
        clusterBrokerService.setClusterBrokerContainer(clusterBrokerContainer);
        ReflectionUtil.setPropertyByReflection(clusterBrokerService, "deviceRegistryServiceUrl", "http://fake.com/fake?deviceId=%s");
        clusterBrokerService.setRestTemplate(restTemplate);
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(RestfulServer.class);
        when(RestfulServer.getContext()).thenReturn(applicationContext);
        when(applicationContext.getBean(anyString())).thenReturn(brokerContainer);
        when(brokerContainer.getConnection()).thenReturn(connection);
    }

    @Test
    public void testGetQueueConnection() {
        Connection conn = clusterBrokerService.getQueueConnection("0");
        Assert.assertEquals(conn, connection);
        verify(brokerContainer, times(1)).setBrokerInfo(anyString(), anyString());
        ///
        clusterBrokerService = new ClusterBrokerServiceImpl();
        clusterBrokerService.setClusterBrokerContainer(clusterBrokerContainer);
        when(clusterBrokerContainer.containsKey(eq("null:0"))).thenReturn(true);
        when(clusterBrokerContainer.get(anyString())).thenReturn(brokerContainer);
        conn = clusterBrokerService.getQueueConnection("0");
        Assert.assertEquals(conn, connection);
        verify(clusterBrokerContainer, times(1)).get(anyString());
        verify(brokerContainer, times(1)).setBrokerInfo(anyString(), anyString());
    }
    @Test
    public void testGetDeviceConnection() {
        DeviceInfo deviceInfo = mock(DeviceInfo.class);
        when(deviceInfo.getBrokerId()).thenReturn("0");
        when(restTemplate.getForObject(anyString(), Matchers.<Class<DeviceInfo>> any(), Matchers.<Object>anyVararg())).thenReturn(deviceInfo);
        when(restTemplate.getForObject(anyString(), Matchers.<Class<DeviceInfo>> any())).thenReturn(deviceInfo);
        clusterBrokerService.setRestTemplate(restTemplate);
        Connection conn = clusterBrokerService.getDeviceConnection("terry");
        Assert.assertEquals(conn, connection);
        verify(brokerContainer, times(1)).setBrokerInfo(anyString(), anyString());
    }
    @Test
    public void testAddQueueConsumer() {
        when(clusterBrokerContainer.containsKey(anyString())).thenReturn(true);
        when(clusterBrokerContainer.get(anyString())).thenReturn(brokerContainer);
        clusterBrokerService.setClusterBrokerContainer(clusterBrokerContainer);
        clusterBrokerService.addQueueConsumer("0", messageConsumer);
        verify(clusterBrokerContainer, times(1)).get(anyString());
    }
    @Test
    public void testQueueConsumerExist() {
        when(clusterBrokerContainer.containsKey(anyString())).thenReturn(true);
        when(clusterBrokerContainer.get(anyString())).thenReturn(brokerContainer);
        when(brokerContainer.getMessageConsumer()).thenReturn(messageConsumer);
        clusterBrokerService.setClusterBrokerContainer(clusterBrokerContainer);
        boolean ret = clusterBrokerService.queueConsumerExist("0");
        Assert.assertTrue(ret);
        //
        when(brokerContainer.getMessageConsumer()).thenReturn(null);
        ret = clusterBrokerService.queueConsumerExist("0");
        Assert.assertFalse(ret);
        //
        when(clusterBrokerContainer.containsKey(anyString())).thenReturn(false);
        ret = clusterBrokerService.queueConsumerExist("0");
        Assert.assertFalse(ret);
    }
    @Test
    public void testCheckBroadcastConnection() {
        when(broadcastClusterBrokerContainer.containsKey(anyString())).thenReturn(true);
        when(broadcastClusterBrokerContainer.get(anyString())).thenReturn(brokerContainer);
        clusterBrokerService.setBroadcastClusterBrokerContainer(broadcastClusterBrokerContainer);
        boolean ret = clusterBrokerService.checkBroadcastConnection("0");
        Assert.assertTrue(true);
        //
        when(broadcastClusterBrokerContainer.containsKey(anyString())).thenReturn(false);
        ret = clusterBrokerService.checkBroadcastConnection("0");
        Assert.assertFalse(ret);
        verify(broadcastClusterBrokerContainer, times(1)).put(anyString(), any(BrokerContainer.class));
    }
    @Mock Iterator<Map.Entry<String, BrokerContainer>> it;
    @Mock Map.Entry<String, BrokerContainer> entry;
    @Mock Set<Map.Entry<String, BrokerContainer>> set;
    @Test
    public void testGetBroadcastConnections() {
        broadcastClusterBrokerContainer = new ConcurrentHashMap<String, BrokerContainer>();
        when(brokerContainer.getConnection()).thenReturn(connection);
        broadcastClusterBrokerContainer.put("0", brokerContainer);
        clusterBrokerService.setBroadcastClusterBrokerContainer(broadcastClusterBrokerContainer);
        List<Connection> list = clusterBrokerService.getBroadcastConnections();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(list.get(0), connection);
    }
}
