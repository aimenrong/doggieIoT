package com.at.amqrouter.common;

import com.at.amqrouter.aspect.AspectProperties;
import com.at.amqrouter.bean.CentralConfEntry;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.at.amqrouter"})
public class SpringOnLoadConfiguration {
    @Bean("RestTemplate")
    public RestTemplate getRestTemplate() {
        System.out.println("Create bean RestTemplate");
        return new RestTemplate();
    }

    @Scope("prototype")
    @Bean("AspectProperties")
    public AspectProperties getAspectProperties() {
        return new AspectProperties();
    }

    /****************************************** Central Configuration Support ********************************************/
    @Bean
    public static PropertyPlaceholderConfigurer properties() {
        String classRootPath = SpringOnLoadConfiguration.class.getResource("/").getPath();
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        List<ClassPathResource> resources = new ArrayList<ClassPathResource>();
        resources.add(new ClassPathResource("router.properties"));
        Properties spcProperties = getSpcProperties("router.properties");
        String registryUrl = spcProperties.getProperty("router.registry.url");
        boolean loadFromCentral = Boolean.valueOf(spcProperties.getProperty("router.central.conf.enabled", "false"));
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
            System.out.println("Load local properties");
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
}
