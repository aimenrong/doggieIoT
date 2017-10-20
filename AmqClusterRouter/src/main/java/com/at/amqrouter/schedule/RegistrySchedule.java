package com.at.amqrouter.schedule;

import com.at.amqrouter.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
@Configuration
@EnableScheduling
public class RegistrySchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrySchedule.class);
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Autowired
    private Provider<HeartBeatAgent> agentProvider;
    private Map<String, HeartBeatAgent> agents = new ConcurrentHashMap<String, HeartBeatAgent>();

    @PostConstruct
    public void init() {
        String[] localServices = System.getProperty(Constants.SERVICE_KEY, "").split(",");
        for (String localService :localServices) {
            String []items = localService.split(":");
            if (items.length == 2) {
                HeartBeatAgent hba = agentProvider.get();
                hba.setServiceId(items[0]);
                hba.setServicePort(Integer.parseInt(items[1]));
                agents.put(hba.getServiceId(), hba);
            }
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void process() {
        Iterator<Map.Entry<String, HeartBeatAgent>> iterator = agents.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HeartBeatAgent> entry = iterator.next();
            executor.submit(entry.getValue());
        }
    }
}
