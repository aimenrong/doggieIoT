package com.at.amqrouter.service.broker;

import com.at.amqrouter.RestfulServer;
import com.at.amqrouter.exception.BrokerException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by eyonlig on 9/26/2017.
 */
@Scope("prototype")
@Component("BrokerContainer")
public class BrokerContainer {
    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    private String brokerId;
    private String brokerUrl;
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    private MessageConsumer messageConsumer;

    private ThreadLocal<Connection> connectionPool = new ThreadLocal<Connection>();
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerContainer.class);

    public Connection getConnection() {
        try {
            Connection connection = connectionPool.get();
            if (null == connection) {
                connection = connectionFactory.createConnection();
                connectionPool.set(connection);
            }
            return validateConnection(connection);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private Connection validateConnection(Connection connection) throws BrokerException {
        try {
            connection.start();
        } catch (Exception e) {
            connectionFactory.setBrokerURL(this.brokerUrl);
            try {
                connection = connectionFactory.createConnection();
                connection.start();
            } catch (Exception e2) {
                throw new BrokerException(e2);
            }
        }
        return connection;
    }

    public void setBrokerInfo(String brokerId, String brokerUrl) {
        this.brokerId = brokerId;
        this.brokerUrl = brokerUrl;
        connectionFactory.setBrokerURL(this.brokerUrl);
    }

    public void setMessageConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }
}
