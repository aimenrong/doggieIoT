package com.at.amqrouter.service.impl;

import com.at.amqrouter.exception.BrokerException;
import com.at.amqrouter.service.BrokerSenderService;
import com.at.amqrouter.service.ClusterBrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/19.
 */
@Component("JmsBrokerSender")
public class JmsBrokerSenderService implements BrokerSenderService {
    @Resource(name = "ClusterBrokerServiceImpl")
    private ClusterBrokerService clusterBrokerService;

    @Value("${general.topic.prefix}")
    private String generalTopicPrefix;
    @Value("${queue.template}")
    private String queueTemplate;

    public void sendTopicMsg(String msg, String topic) throws BrokerException {
        try {
            List<Connection> connections = clusterBrokerService.getBroadcastConnections();
            for (Connection connection : connections) {
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue("notification-topic");
                destination = session.createTopic(generalTopicPrefix + topic);
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                String text = msg;
                TextMessage message = session.createTextMessage(text);
                System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);
                session.close();
            }
        }
        catch (Exception e) {
            throw new BrokerException("Fail to send topic message to devices...", e);
        }
    }

    public void sendQueueMsg(String msg, String clientId) throws BrokerException {
        try {
            Connection connection = clusterBrokerService.getDeviceConnection(clientId);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(String.format(queueTemplate, clientId));
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            String text = msg;
            TextMessage message = session.createTextMessage(text);
            System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);
            session.close();
        }
        catch (Exception e) {
            throw new BrokerException("Fail to send queue message to device...", e);
        }
    }
}
