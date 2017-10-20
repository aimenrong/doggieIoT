package com.at.amqrouter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.at.amqrouter.bean.DeviceEvent;
import com.at.amqrouter.service.BrokerConsumerService;
import com.at.amqrouter.service.ClusterBrokerService;
import com.at.amqrouter.service.SpcService;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Service("JmsConsumerService")
public class JmsConsumerService implements BrokerConsumerService, MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConsumerService.class);
    private String brokerServiceUrl;
    @Value("${broker.consumer.queue}")
    private String consumerQueue;
    @Resource(name = "ClusterBrokerServiceImpl")
    private ClusterBrokerService clusterBrokerService;
    @Resource(name = "KafkaSpcService")
    private SpcService<DeviceEvent> spcService;

    public void consumeMessage(String brokerId) {
        try {
            if (!clusterBrokerService.queueConsumerExist(brokerId)) {
                LOGGER.info("Start to setup consumer on broker {}", brokerId);
                Connection connection = clusterBrokerService.getQueueConnection(brokerId);
                Session session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(consumerQueue);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(this);
                clusterBrokerService.addQueueConsumer(brokerId, consumer);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                LOGGER.info("Received message"
                        + textMessage.getText() + "'");
            } else {
                ActiveMQBytesMessage amqMsg = (ActiveMQBytesMessage) message;
                String content = new String(amqMsg.getContent().getData());
                DeviceEvent deviceEvent = JSON.parseObject(content, new TypeReference<DeviceEvent>() {
                });
                spcService.sendMessage(deviceEvent);
                LOGGER.info(content);
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
