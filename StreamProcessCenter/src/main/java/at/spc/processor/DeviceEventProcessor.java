package at.spc.processor;

import at.spc.aspect.AspectRestTemplate;
import at.spc.bean.DeviceEvent;
import at.spc.bean.DeviceNotification;
import at.spc.bean.SubscriptionBean;
import at.spc.intf.SubFunction;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Created by Terry LIANG on 2017/9/4.
 */
public class DeviceEventProcessor implements Processor<String, String>, SubFunction {
    private static final String FUNCTION_ID = "DeviceEventProcessor";

    private ProcessorContext context;
    private Map<String, String> kvStore = new ConcurrentHashMap<String, String>();
    @Value("${spc.global.distance.meter:10}")
    private int globalDistanceInMeter;
    @Resource(name = "AspectRestTemplate")
    private AspectRestTemplate restTemplate;
    @Value("${registry.lookup.subscription.url}")
    private String registryLookupSubscriptionUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceEventProcessor.class);

    @Override
    public String getFunctionId() {
        return FUNCTION_ID;
    }

    public void init(ProcessorContext context) {
        LOGGER.info("Call DeviceEventProcessor init");
        this.context = context;
//        this.context.schedule(5000);
    }

    public void process(String key, String value) {
        LOGGER.info(String.format("key = %s, value = %s", key, value));
        DeviceEvent deviceEvent = JSON.parseObject(value, DeviceEvent.class);
        if (null != deviceEvent) {
            String nextDestStr = match(deviceEvent);
            if (null != nextDestStr) {
                LOGGER.info(String.format("%s = %s", "key", nextDestStr));
//                this.kvStore.put(nextDestStr, nextDestStr);
                context.forward(key, nextDestStr);
                context.commit();
            }
        }
    }

    public void punctuate(long timestamp) {
        LOGGER.info("Call punctuate");
        Iterator<Map.Entry<String, String>> iter = this.kvStore.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            context.forward(entry.getKey(), entry.getKey());
        }
        // commit the current processing progress
        context.commit();
    }

    public void close() {
        LOGGER.info("Call close");
    }

    private String match(DeviceEvent deviceEvent) {
        String deviceId = deviceEvent.getDeviceId();
        List<SubscriptionBean> subscriptionBeanList = lookupSubscriptions(deviceId);
        List<DeviceNotification> availableList = new LinkedList<>();
        subscriptionBeanList.forEach(subscriptionBean -> {
            if (distance(deviceEvent.getLongitude(), deviceEvent.getLatitude(), subscriptionBean.getLongitude(), subscriptionBean.getLatitude()) <= globalDistanceInMeter) {
                DeviceNotification deviceNotification = new DeviceNotification();
                deviceNotification.setDeviceId(deviceId);
                deviceNotification.setFunctionId(subscriptionBean.getFunctionId());
                deviceNotification.setLongitude(subscriptionBean.getLongitude());
                deviceNotification.setLatitude(subscriptionBean.getLatitude());
                deviceNotification.setContent(subscriptionBean.getContent());
                deviceNotification.setCreatedTime(new Date());
                deviceNotification.setSubscriptionId(deviceNotification.getSubscriptionId());
                availableList.add(deviceNotification);
            }
        });
        if (availableList.size() == 0) {
            return null;
        }
        return JSON.toJSONString(availableList);
    }

    private List<SubscriptionBean> lookupSubscriptions(String deviceId) {
        ParameterizedTypeReference<List<SubscriptionBean>> typeRef = new ParameterizedTypeReference<List<SubscriptionBean>>() {
        };
        ResponseEntity<List<SubscriptionBean>> responseEntity = restTemplate.exchange(String.format(this.registryLookupSubscriptionUrl, deviceId),
                HttpMethod.GET, null, typeRef);
        List<SubscriptionBean> subscriptionBeanList = responseEntity.getBody();
        return subscriptionBeanList;
    }

    /**
     * To calculate the distance between 2 points on the earth
     *
     * @param long1 longitude1
     * @param lat1  latitude1
     * @param long2 longitude2
     * @param lat2  latitude2
     * @return the distance with unit meter
     */
    private int distance(double long1, double lat1, double long2,
                         double lat2) {
        double a, b, R;
        R = 6378137; // R of earth
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        int d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = (int) (2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2)));
        return d;
    }
}
