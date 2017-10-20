package at.spc.common;

import at.spc.intf.impl.DeviceEventFunction;
import at.spc.intf.impl.DeviceNotificationFunction;
import at.spc.processor.DeviceEventProcessor;
import at.spc.processor.DeviceNotificationProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Terry LIANG on 2017/9/22.
 */
@Configuration
public class DeviceNotificationConfiguration {
    @Bean("DeviceEventFunction")
    public DeviceEventFunction getDeviceEventFunction() {
        return new DeviceEventFunction();
    }

    @Bean("DeviceNotificationFunction")
    public DeviceNotificationFunction getDeviceNotificationFunction() {
        return new DeviceNotificationFunction();
    }

    @Bean("DeviceEventProcessor")
    public DeviceEventProcessor getDeviceEventProcessor() {
        return new DeviceEventProcessor();
    }

    @Bean("DeviceNotificationProcessor")
    public DeviceNotificationProcessor getDeviceNotificationProcessor() {
        return new DeviceNotificationProcessor();
    }
}
