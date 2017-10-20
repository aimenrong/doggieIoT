package com.at.amqrouter.exception;

/**
 * Created by Terry LIANG on 2017/9/26.
 */
public class BrokerException extends Exception {
    public BrokerException(Exception e) {
        super(e);
    }

    public BrokerException(String message, Exception e) {
        super(message, e);
    }
}
