package com.at.registry.common;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Terry LIANG on 2017/9/17.
 */
@Configuration
@ComponentScan(basePackages = {"com.at.registry"})
public class SpringJavaConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringJavaConfiguration.class);

    @Value("${registry.mongo.host:localhost}")
    private String mongoHost;
    @Value("${registry.mongo.port:27017}")
    private int mongoPort;
    @Value("${registry.database.name:registry}")
    private String registryDb;

    @Bean
    public static PropertyPlaceholderConfigurer properties() {
        LOGGER.info("Start to load place holder...");
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ClassPathResource[] resources = new ClassPathResource[]
                {new ClassPathResource("registry.properties")};
        ppc.setLocations(resources);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongoHost, mongoPort), registryDb);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
