package com.at.amqrouter.service;

/**
 * Created by eyonlig on 9/27/2017.
 */
public interface BrokerConsumerService {
    void consumeMessage(String brokerId);
}
