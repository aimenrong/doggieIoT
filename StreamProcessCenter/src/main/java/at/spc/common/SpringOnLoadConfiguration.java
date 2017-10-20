package at.spc.common;

import at.spc.App;
import at.spc.aspect.AspectProperties;
import at.spc.aspect.AspectRestTemplate;
import at.spc.bean.CentralConfEntry;
import at.spc.bean.FunctionMeta;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("at.spc.aspect")
public class SpringOnLoadConfiguration {
    @Bean
    public static PropertyPlaceholderConfigurer properties(){
        String classRootPath = SpringOnLoadConfiguration.class.getResource("/").getPath();
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        List<ClassPathResource> resources = new ArrayList<ClassPathResource>();
        resources.add(new ClassPathResource("spc.properties"));
        Properties spcProperties = getSpcProperties("spc.properties");
        String registryUrl = spcProperties.getProperty("spc.registry.url");
        boolean loadFromCentral = Boolean.valueOf(spcProperties.getProperty("spc.central.conf.enabled", "false"));
        if (loadFromCentral) {
            Properties localProperties = getSpcProperties("central.properties");
            Enumeration<Object> it = localProperties.keys();
            Properties centralProperties = new Properties();
            while (it.hasMoreElements()) {
                String propKey = (String) it.nextElement();
                String remotePropValue = getRemotePropertyValue(registryUrl, propKey);
                centralProperties.setProperty(propKey, remotePropValue);
            }
            storeRemoteToLocal(centralProperties, classRootPath + "/" + "central-remote.properties");
            resources.add(new ClassPathResource("central-remote.properties"));
        } else {
            resources.add(new ClassPathResource("central.properties"));
        }
        ppc.setLocations( resources.toArray(new ClassPathResource[0]) );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

    private static Properties getSpcProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(new ClassPathResource(path).getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void storeRemoteToLocal(Properties properties, String path) {
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(path);
            properties.store(fos, "central configuration");
            fos.close();
        } catch (Exception e) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e2){}
            }
            e.printStackTrace();
        }
    }
    private static RestTemplate restTemplate = new RestTemplate();
    private static String getRemotePropertyValue(String url, String key) {
        CentralConfEntry centralConfEntry = restTemplate.getForObject(String.format("%s/registry/getProperty?key=%s", url, key), CentralConfEntry.class);
        if (centralConfEntry != null) {
            return (String) centralConfEntry.getValue();
        }
        return null;
    }

    /***************************************** Split Line *********************************************/

    @Bean("AspectRestTemplate")
    public AspectRestTemplate getAspectRestTemplate() {
        return new AspectRestTemplate();
    }

    @Bean("RestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Scope("prototype")
    @Bean("AspectProperties")
    public AspectProperties getAspectProperties() {
        return new AspectProperties();
    }

    @Bean("SystemMessageFunctionMeta")
    public FunctionMeta getSystemMessageFunctionMeta() {
        FunctionMeta functionMeta = new FunctionMeta();
        functionMeta.setFunctionChainId("systemFunctionChain");
        functionMeta.setFunctionBeanNames(new String[]{"SystemMessageFunction"});
        functionMeta.setFunctionConfigurationClassName("at.spc.common.SystemFunctionConfiguration");
        functionMeta.getSubFunctionBeanNames().put("SystemMessageFunction", new String[]{"SystemMessageProcessor"});
        return functionMeta;
    }

    @Bean("DeviceEventFunctionMeta")
    public FunctionMeta getDeviceEventFunctionMeta() {
        FunctionMeta functionMeta = new FunctionMeta();
        functionMeta.setFunctionChainId("deviceNotificationFunctionChain");
        functionMeta.setFunctionConfigurationClassName("at.spc.common.DeviceNotificationConfiguration");
        functionMeta.setFunctionBeanNames(new String[]{"DeviceEventFunction", "DeviceNotificationFunction"});
        functionMeta.getSubFunctionBeanNames().put("DeviceEventFunction", new String[]{"DeviceEventProcessor"});
        functionMeta.getSubFunctionBeanNames().put("DeviceNotificationFunction", new String[]{"DeviceNotificationProcessor"});
        return functionMeta;
    }
}
