package at.spc.processor;

import at.spc.aspect.AspectRestTemplate;
import at.spc.bean.DeviceNotification;
import at.spc.bean.GenernalRestResponse;
import at.spc.intf.SubFunction;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
public class DeviceNotificationProcessor implements Processor<String, String>, SubFunction {
    private static final String FUNCTION_ID = "DeviceNotificationProcessor";
    private ProcessorContext context;
    private Map<String, String> kvStore = new ConcurrentHashMap<String, String>();
    private final long MIN_DIST = 10;

    @Resource(name = "AspectRestTemplate")
    private AspectRestTemplate restTemplate;
    @Value("${amqrouter.send.notification.url}")
    private String routerRestUrl;
    @Value("${registry.subscription.delete.url}")
    private String subscriptionDeleteUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceNotificationProcessor.class);

    @Override
    public String getFunctionId() {
        return FUNCTION_ID;
    }

    public void init(ProcessorContext context) {
        LOGGER.info("Call DeviceNotificationProcessor init");
        this.context = context;
    }

    public void process(String key, String value) {
        LOGGER.info(String.format("key = %s", key));
        LOGGER.info(String.format("value = %s", value));
        try {
            List<DeviceNotification> deviceNotifications = JSON.parseArray(value, DeviceNotification.class);
            if (null != deviceNotifications) {
                processFunc(deviceNotifications.toArray(new DeviceNotification[0]));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void punctuate(long timestamp) {
    }

    public void close() {
        LOGGER.info("Call close");
    }

    private void processFunc(DeviceNotification[] deviceNotifications) {
        // Send notification to AmqRouter
        if (deviceNotifications != null) {
            for (DeviceNotification deviceNotification : deviceNotifications) {
                GenernalRestResponse genernalRestResponse = restTemplate.postForObject(routerRestUrl,
                        deviceNotification, GenernalRestResponse.class);
                LOGGER.info(genernalRestResponse.getMessage());
                if (genernalRestResponse.getStatusCode() == 200) {
                    try {
                        LOGGER.debug("Process subscription with subscriptionId %s successfully, get ready to delete it.", deviceNotification.getSubscriptionId());
                        restTemplate.delete(String.format(subscriptionDeleteUrl, deviceNotification.getSubscriptionId()));
                        LOGGER.debug("Delete subscription with subscriptionId %s successfully", deviceNotification.getSubscriptionId());
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
