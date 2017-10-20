package com.at.registry.service;

import com.at.registry.exception.ServiceException;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public interface CentralConfRegistryService {
    void set(String key, Object value) throws ServiceException;

    void set(String key, Object value, String description) throws ServiceException;

    Object get(String key) throws ServiceException;
}
