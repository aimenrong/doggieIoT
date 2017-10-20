package com.at.amqrouter.service;

import com.at.amqrouter.RestfulServer;
import com.at.amqrouter.service.broker.BrokerContainer;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/10/2.
 */
public interface ClusterBrokerService {
    Connection getDeviceConnection(String deviceId) ;

    Connection getQueueConnection(String brokerId);

    /**
     * VirtualTopic ( Queue ) listener
     * @param brokerId( nodeId )
     * @param messageConsumer
     */
    void addQueueConsumer(String brokerId, MessageConsumer messageConsumer);

    boolean queueConsumerExist(String brokerId);

    void removeQueueConsumer(String brokerId);

    boolean checkBroadcastConnection(String brokerId);

    List<Connection> getBroadcastConnections();
}
