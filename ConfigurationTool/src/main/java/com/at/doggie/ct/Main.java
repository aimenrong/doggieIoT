package com.at.doggie.ct;

import com.at.doggie.ct.common.SpringJavaConfiguration;
import com.at.doggie.ct.service.CentralConfRegistryService;
import com.at.doggie.ct.service.impl.CentralConfRegistryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.*;

/**
 * Created by Terry LIANG on 2017/10/18.
 */
public class Main {
    private AnnotationConfigApplicationContext applicationContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public void init() {
        try {
            applicationContext = new AnnotationConfigApplicationContext();
            applicationContext.register(SpringJavaConfiguration.class);
            applicationContext.refresh();
            Properties properties = getCentralProperties();
            CentralConfRegistryService centralConfRegistryService = (CentralConfRegistryServiceImpl)applicationContext.getBean("CentralConfRegistryServiceImpl");
            Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = it.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                centralConfRegistryService.set(key, value);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private Properties getCentralProperties() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("central.properties");
        Properties properties = new Properties();
        properties.load(classPathResource.getInputStream());
        return properties;
    }

    public static void main(String[] args) {
        new Main().init();
    }
}
