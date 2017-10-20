package com.at.registry.service.impl;

import com.at.registry.dao.impl.CentralConfRegistryMongoDao;
import com.at.registry.exception.DaoException;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.CentralConfRegistryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
@Service("CentralConfRegistryServiceImpl")
public class CentralConfRegistryServiceImpl implements CentralConfRegistryService {
    @Resource(name = "CentralConfRegistryMongoDao")
    private CentralConfRegistryMongoDao centralConfRegistryMongoDao;


    public void set(String key, Object value) throws ServiceException {
        set(key, value, null);
    }

    public void set(String key, Object value, String description) throws ServiceException {
        try {
            centralConfRegistryMongoDao.setConf(key, value, description);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public Object get(String key) throws ServiceException {
        try {
            return centralConfRegistryMongoDao.getConf(key);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
