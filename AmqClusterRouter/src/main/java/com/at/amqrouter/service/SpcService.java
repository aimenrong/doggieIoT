package com.at.amqrouter.service;

/**
 * Created by eyonlig on 9/27/2017.
 */
public interface SpcService<T> {

    void sendMessage(String key, String value);

    void sendMessage(T message);
}
