package com.at.kafka.hc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.*;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.*;

/**
 * Created by eyonlig on 9/29/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestKafkaHCAgent {
    private String brokerIdPath = "/brokers/ids";
    private KafkaHCAgent kafkaHCAgent;
    @Mock
    CuratorFramework mockClient;
    @Mock
    TreeCache mockCache;
    @Mock
    Listenable<TreeCacheListener> mockListenable;
    @Mock
    TreeCacheEvent mockEvent;
    @Mock
    ChildData mockChildData;
    TreeCacheListener treeCacheListener;

    @Before
    public void init() throws Exception {
        kafkaHCAgent = new KafkaHCAgent();

        setPropertyByReflection(kafkaHCAgent, "client", mockClient);
        setPropertyByReflection(kafkaHCAgent, "cache", mockCache);
        setPropertyByReflection(kafkaHCAgent, "nodeId", "0");
        setPropertyByReflection(kafkaHCAgent, "subNodeId", "0");
        setPropertyByReflection(kafkaHCAgent, "zkConnString", "localhost:2181");
        Mockito.when(mockCache.getListenable()).thenReturn(mockListenable);
        Mockito.when(mockEvent.getData()).thenReturn(mockChildData);
        kafkaHCAgent.init();
        treeCacheListener = (TreeCacheListener) getPropertyByReflection(kafkaHCAgent, "listener");

    }

    @Test
    public void testAddNode() {
        try {
            Mockito.when(mockEvent.getType()).thenReturn(NODE_ADDED);
            Mockito.when(mockChildData.getPath()).thenReturn(brokerIdPath + "/0");
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            treeCacheListener.childEvent(mockClient, mockEvent);
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            Assert.assertEquals(map.containsKey("0"), true);
            Assert.assertEquals("a-PC:9092", map.get("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemoveNode() {
        try {
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            map.put("0", "");
            map.put("1", "");
            Mockito.when(mockEvent.getType()).thenReturn(NODE_REMOVED);
            Mockito.when(mockChildData.getPath()).thenReturn(brokerIdPath + "/0");
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            treeCacheListener.childEvent(mockClient, mockEvent);

            Assert.assertEquals(map.size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateNode() {
        try {
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            map.put("0", "");
            Mockito.when(mockEvent.getType()).thenReturn(NODE_UPDATED);
            Mockito.when(mockChildData.getPath()).thenReturn(brokerIdPath + "/0");
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            treeCacheListener.childEvent(mockClient, mockEvent);
            Assert.assertEquals(map.containsKey("0"), true);
            Assert.assertEquals("a-PC:9092", map.get("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddNonHostNodeWithNonMultiMode() {
        try {
            setPropertyByReflection(kafkaHCAgent, "enableMultiNodeHC", Boolean.valueOf("true"));
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            map.put("0", "");
            Mockito.when(mockEvent.getType()).thenReturn(NODE_ADDED);
            Mockito.when(mockChildData.getPath()).thenReturn(brokerIdPath + "/1");
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            treeCacheListener.childEvent(mockClient, mockEvent);

            Assert.assertEquals(map.size(), 2);
            Assert.assertEquals(map.containsKey("1"), true);
            Assert.assertEquals(map.get("1"), "a-PC:9092");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddNonHostNodeWithMultiMode() {
        try {
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            Mockito.when(mockEvent.getType()).thenReturn(NODE_ADDED);
            Mockito.when(mockChildData.getPath()).thenReturn(brokerIdPath + "/0");
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            treeCacheListener.childEvent(mockClient, mockEvent);

            Assert.assertEquals(map.size(), 1);
            Assert.assertEquals(map.containsKey("0"), true);
            Assert.assertEquals("a-PC:9092", map.get("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListWhenInit() {
        try {
            Map<String, String> map = kafkaHCAgent.getBootstrapList();
            Map<String, ChildData> testMap = new HashMap<>();
            testMap.put("0", mockChildData);
            Mockito.when(mockCache.getCurrentChildren(brokerIdPath)).thenReturn(testMap);
            KafkaInfo kafkaInfo = new KafkaInfo();
            kafkaInfo.host = "a-PC";
            kafkaInfo.port = 9092;
            Mockito.when(mockChildData.getData()).thenReturn(JSON.toJSONString(kafkaInfo).getBytes());
            Mockito.when(mockEvent.getType()).thenReturn(INITIALIZED);

            setPropertyByReflection(kafkaHCAgent, "enableListWhenInit", Boolean.valueOf("true"));
            treeCacheListener.childEvent(mockClient, mockEvent);
            Assert.assertEquals(map.size(), 1);
            Assert.assertEquals(map.containsKey("0"), true);
            Assert.assertEquals("a-PC:9092", map.get("0"));
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
