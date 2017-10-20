package at.spc.test.processor;

import at.spc.aspect.AspectRestTemplate;
import at.spc.bean.DeviceEvent;
import at.spc.bean.DeviceNotification;
import at.spc.bean.SubscriptionBean;
import at.spc.processor.DeviceEventProcessor;
import at.spc.test.util.ReflectionUtil;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsMetrics;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateRestoreCallback;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TaskId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Terry LIANG on 2017/10/4.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDeviceEventProcessor {
    @Mock AspectRestTemplate restTemplate;
    DeviceEventProcessor eventProcessor;
    List<SubscriptionBean> subscriptionBeanList;
    ResponseEntity responseEntity;
    TestContext context;

    @Before
    public void init() {
        context = new TestContext();
        eventProcessor = new DeviceEventProcessor();
        ReflectionUtil.setPropertyByReflection(eventProcessor, "registryLookupSubscriptionUrl", "http://localhost:2222/registry/lookupSubscription/device/%s");
        subscriptionBeanList = new LinkedList<>();
        SubscriptionBean subscriptionBean = new SubscriptionBean();
        subscriptionBean.setDeviceId("terry");
        subscriptionBean.setFunctionId("notificationFunction");
        subscriptionBean.setContent("Hello World");
        subscriptionBean.setLongitude(85.3);
        subscriptionBean.setLatitude(10.2);
        subscriptionBeanList.add(subscriptionBean);
        //
        subscriptionBean = new SubscriptionBean();
        subscriptionBean.setDeviceId("terry2");
        subscriptionBean.setFunctionId("notificationFunction");
        subscriptionBean.setContent("Hello World2");
        subscriptionBean.setLongitude(11.3);
        subscriptionBean.setLatitude(80.2);
        subscriptionBeanList.add(subscriptionBean);
        //
        subscriptionBean = new SubscriptionBean();
        subscriptionBean.setDeviceId("terry2");
        subscriptionBean.setFunctionId("notificationFunction");
        subscriptionBean.setContent("Hello World2");
        subscriptionBean.setLongitude(85.3);
        subscriptionBean.setLatitude(10.3);
        subscriptionBeanList.add(subscriptionBean);

        responseEntity = new ResponseEntity(subscriptionBeanList, HttpStatus.ACCEPTED);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), Matchers.<ParameterizedTypeReference<List<SubscriptionBean>>>any(),
                Matchers.<Object>anyVararg())).thenReturn(responseEntity);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), Matchers.<ParameterizedTypeReference<List<SubscriptionBean>>>any())).thenReturn(responseEntity);

    }

    @Test
    public void testProcess() {
        DeviceEvent testEvent = new DeviceEvent();
        testEvent.setDeviceId("terry");
        testEvent.setLatitude(10.2);
        testEvent.setLongitude(85.3);
        testEvent.setContent("Unit Test with DeviceEventProcessor");
        ReflectionUtil.setPropertyByReflection(eventProcessor, "restTemplate", restTemplate);
        eventProcessor.init(context);
        eventProcessor.process("terry", JSON.toJSONString(testEvent));
        System.out.println(context.value);
        DeviceNotification[] deviceNotifications = JSON.parseObject(context.value, DeviceNotification[].class);
        Assert.assertEquals(1, deviceNotifications.length);
        Assert.assertEquals(deviceNotifications[0].getLatitude(), 10.2, 0.1);
        Assert.assertEquals(deviceNotifications[0].getLongitude(), 85.3, 0.1);
        Assert.assertEquals("Hello World", deviceNotifications[0].getContent());
    }

}
class TestContext implements ProcessorContext {
    public String key, value;
    @Override
    public String applicationId() {
        return null;
    }

    @Override
    public TaskId taskId() {
        return null;
    }

    @Override
    public Serde<?> keySerde() {
        return null;
    }

    @Override
    public Serde<?> valueSerde() {
        return null;
    }

    @Override
    public File stateDir() {
        return null;
    }

    @Override
    public StreamsMetrics metrics() {
        return null;
    }

    @Override
    public void register(StateStore stateStore, boolean b, StateRestoreCallback stateRestoreCallback) {

    }

    @Override
    public StateStore getStateStore(String s) {
        return null;
    }

    @Override
    public void schedule(long l) {

    }

    @Override
    public <K, V> void forward(K k, V v) {
        this.key = (String)k;
        this.value = (String)v;
    }

    @Override
    public <K, V> void forward(K k, V v, int i) {

    }

    @Override
    public <K, V> void forward(K k, V v, String s) {

    }

    @Override
    public void commit() {

    }

    @Override
    public String topic() {
        return null;
    }

    @Override
    public int partition() {
        return 0;
    }

    @Override
    public long offset() {
        return 0;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    @Override
    public Map<String, Object> appConfigs() {
        return null;
    }

    @Override
    public Map<String, Object> appConfigsWithPrefix(String s) {
        return null;
    }
}
