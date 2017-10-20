package com.at.registry.exception;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public class ServiceException extends Exception {
    public ServiceException(Exception e) {
        super(e);
    }
    public ServiceException(String e) {
        super(e);
    }
}
