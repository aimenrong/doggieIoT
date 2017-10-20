package com.at.amqrouter.service;

import com.at.amqrouter.exception.BrokerException;

/**
 * Created by Terry LIANG on 2017/9/19.
 */
public interface BrokerSenderService {
    void sendQueueMsg(String msg, String clientId) throws BrokerException;

    void sendTopicMsg(String msg, String topic) throws BrokerException;
}
