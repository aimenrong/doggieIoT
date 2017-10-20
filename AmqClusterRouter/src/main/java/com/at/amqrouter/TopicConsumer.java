package com.at.amqrouter;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * Created by eyonlig on 9/12/2017.
 */
public class TopicConsumer implements MessageListener {
    private TopicConnection connection;

    public TopicConsumer() {
        try {
            // Getting JMS connection from the server

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();

            // need to setClientID value, any string value you wish
            connection.setClientID("12345");

            try{
                connection.start();
            }catch(Exception e){
                System.err.println("NOT CONNECTED!!!");
            }
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic("event-topic");

            //need to use createDurableSubscriber() method instead of createConsumer() for topic
//             MessageConsumer consumer = session.createConsumer(topic);
            MessageConsumer consumer = session.createDurableSubscriber(topic,
                    "consumer1");

            consumer.setMessageListener(this);
            //connection.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void main(String []args) {
        new TopicConsumer();
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message"
                        + textMessage.getText() + "'");
            } else {
                System.out.println(message);
            }
        } catch (JMSException e) {
            System.out.println("Caught:" + e);
            e.printStackTrace();
        }
    }
}
