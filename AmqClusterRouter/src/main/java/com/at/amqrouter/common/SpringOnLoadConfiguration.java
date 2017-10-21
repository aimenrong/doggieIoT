package com.at.amqrouter.common;

import com.at.amqrouter.aspect.AspectProperties;
import com.at.amqrouter.bean.CentralConfEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringOnLoadConfiguration.class);
    private static final String CONFIG_PATH_KEY = "config.path";
    private static final String DEFAULT_CENTRAL_PROP_NAME = "central.properties";
    private static final String DEFAULT_REMOTE_CENTRAL_PROP_NAME = "central-remote.properties";

    @Bean("RestTemplate")
    public RestTemplate getRestTemplate() {
        LOGGER.info("Create bean RestTemplate");
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
        String configPath = System.getProperty(CONFIG_PATH_KEY);
        if (null == configPath) {
            throw new IllegalArgumentException(String.format("Cannot find the config file"));
        }
        String configRootPath = configPath.substring(0, configPath.lastIndexOf("/"));

        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(new FileSystemResource(configPath));
        Properties spcProperties = getFileAsProperties(configPath);
        String registryUrl = spcProperties.getProperty("router.registry.url");
        boolean loadFromCentral = Boolean.valueOf(spcProperties.getProperty("router.central.conf.enabled", "false"));
        if (loadFromCentral) {
            Properties localProperties = getFileAsProperties(DEFAULT_CENTRAL_PROP_NAME);
            Enumeration<Object> it = localProperties.keys();
            Properties centralProperties = new Properties();
            while (it.hasMoreElements()) {
                String propKey = (String) it.nextElement();
                String remotePropValue = getRemotePropertyValue(registryUrl, propKey);
                centralProperties.setProperty(propKey, remotePropValue);
            }
            storeRemoteToLocal(centralProperties, configRootPath + "/" + DEFAULT_REMOTE_CENTRAL_PROP_NAME);
            resources.add(new FileSystemResource(configRootPath + "/" + DEFAULT_REMOTE_CENTRAL_PROP_NAME));
        } else {
            LOGGER.info("Load local properties");
            resources.add(new FileSystemResource(configRootPath + "/" + DEFAULT_CENTRAL_PROP_NAME));
        }
        ppc.setLocations( resources.toArray(new FileSystemResource[0]) );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

    private static Properties getFileAsProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(path)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
