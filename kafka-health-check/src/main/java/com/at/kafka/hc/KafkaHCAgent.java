package com.at.kafka.hc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/28.
 */
@Component("KafkaHCAgent")
public class KafkaHCAgent {
    private Map<String, String> bootstrapList = new ConcurrentHashMap<String, String>();

    private CuratorFramework client;
    private TreeCache cache;
    private TreeCacheListener listener;

    private String brokerIdPath = "/brokers/ids";
    @Value("${hc.host.node.id}")
    private String nodeId;
    @Value("${hc.host.subnode.id}")
    private String subNodeId;
    @Value("${zookeeper.connect:localhost:2181}")
    private String zkConnString;
    @Value("${hc.multi.node.enabled:false}")
    private boolean enableMultiNodeHC;
    @Value("${hc.list.when.init.enabled:false}")
    private boolean enableListWhenInit;

    @PostConstruct
    public void init() {
        try {
            if (null == client) {
                client = CuratorFrameworkFactory.newClient(zkConnString,
                        new ExponentialBackoffRetry(1000, 3));
            }
            if (null == cache) {
                cache = new TreeCache(client, brokerIdPath);
            }
            addListener();
            cache.start();
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addListener() {
        listener = new TreeCacheListener() {
            public void childEvent(CuratorFramework client, TreeCacheEvent event)
                    throws Exception {
                switch (event.getType()) {
                    case NODE_ADDED: {
                        System.out.println("Add event");
                        processEvent(event, false);
                        break;
                    }
                    case NODE_UPDATED: {
                        System.out.println("Update event");
                        processEvent(event, false);
                        break;
                    }
                    case NODE_REMOVED: {
                        System.out.println("Remove event");
                        processEvent(event, true);
                        break;
                    }
                    case CONNECTION_SUSPENDED: {
                        cleanupBootstrapList();
                        break;
                    }
                    default: {
                        System.out.println("Other event: " + event.getType().name());
                        if (enableListWhenInit) {
                            listWhenInit();
                        }
                    }
                }
            }

        };
        cache.getListenable().addListener(listener);
    }

    private void cleanupBootstrapList() {
        this.bootstrapList.clear();
    }

    private void processEvent(TreeCacheEvent event, boolean removed) {
        String nodeId = ZKPaths.getNodeFromPath(event.getData().getPath());
        String value = new String(event.getData().getData());
        if (!enableMultiNodeHC && !subNodeId.equals(nodeId)) {
            return;
        }
        KafkaInfo kafkaInfo = (KafkaInfo) JSON.parseObject(value, new TypeReference<KafkaInfo>() {
        });
        if (removed) {
            if (bootstrapList.containsKey(nodeId)) {
                System.out.println("Remove " + kafkaInfo);
                bootstrapList.remove(nodeId);
            }
        } else {
            System.out.println("Add or update " + kafkaInfo);
            bootstrapList.put(nodeId, formatBoostrapUrl(kafkaInfo.host, kafkaInfo.port));
        }
    }

    private String formatBoostrapUrl(String host, int port) {
        return String.format("%s:%d", host, port);
    }

    private void listWhenInit() {
        if (cache.getCurrentChildren(brokerIdPath).size() == 0) {
            System.out.println("* empty *");
        } else {
            Map<String, ChildData> map = cache.getCurrentChildren(brokerIdPath);
            Set<Map.Entry<String, ChildData>> set = map.entrySet();
            for (Map.Entry<String, ChildData> entry : set) {
                String nodeId = entry.getKey();
                KafkaInfo kafkaInfo = (KafkaInfo) JSON.parseObject(new String(entry.getValue().getData()), new TypeReference<KafkaInfo>() {
                });
                if (!enableMultiNodeHC && !subNodeId.equals(nodeId)) {
                    continue;
                }
                bootstrapList.put(nodeId, formatBoostrapUrl(kafkaInfo.host, kafkaInfo.port));
            }
        }
    }

    public Map<String, String> getBootstrapList() {
        return bootstrapList;
    }
}

class KafkaInfo {
    public String host;
    public int port;

    @Override
    public String toString() {
        return "KafkaInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}