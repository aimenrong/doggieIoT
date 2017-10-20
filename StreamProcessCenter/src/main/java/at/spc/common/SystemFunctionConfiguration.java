package at.spc.common;

import at.spc.intf.impl.DeviceNotificationFunction;
import at.spc.intf.impl.DeviceEventFunction;
import at.spc.intf.impl.SystemMessageFunction;
import at.spc.processor.DeviceNotificationProcessor;
import at.spc.processor.DeviceEventProcessor;
import at.spc.processor.SystemMessageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by eyonlig on 9/22/2017.
 */
@Configuration
public class SystemFunctionConfiguration {

    @Bean("SystemMessageFunction")
    public SystemMessageFunction getSystemMessageFunction() {
        return new SystemMessageFunction();
    }

    @Bean("SystemMessageProcessor")
    public SystemMessageProcessor getSystemMessageProcessor() {
        return new SystemMessageProcessor();
    }
}
