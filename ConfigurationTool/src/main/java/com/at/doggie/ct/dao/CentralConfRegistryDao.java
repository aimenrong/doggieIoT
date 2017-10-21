package com.at.doggie.ct.dao;


import com.at.doggie.ct.exception.DaoException;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public interface CentralConfRegistryDao {
    void setConf(String key, Object value, String description) throws DaoException;

    Object getConf(String key) throws DaoException;
}
