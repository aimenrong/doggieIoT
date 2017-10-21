package com.at.doggie.ct.dao.impl;

import com.at.doggie.ct.bean.CentralConfEntry;
import com.at.doggie.ct.dao.CentralConfRegistryDao;
import com.at.doggie.ct.dao.MongoBaseDao;
import com.at.doggie.ct.exception.DaoException;
import com.at.doggie.ct.exception.MongoDaoException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
@Component("CentralConfRegistryMongoDao")
public class CentralConfRegistryMongoDao extends MongoBaseDao<CentralConfEntry> implements CentralConfRegistryDao {
    public void setConf(String key, Object value, String description) throws DaoException {
        try {
            CentralConfEntry centralConfEntry = new CentralConfEntry();
            centralConfEntry.setKey(key);
            centralConfEntry.setValue(value);
            centralConfEntry.setLastModifiedTime(new Date());
            centralConfEntry.setDescription(description);
            CentralConfEntry centralConfEntry2 = super.findOneByParams(new String[]{"key"}, new Object[]{key}, CentralConfEntry.class);
            if (null == centralConfEntry2) {
                centralConfEntry.setCreatedTime(new Date());
            } else {
                if (null == description) {
                    centralConfEntry.setDescription(centralConfEntry2.getDescription());
                } else {
                    centralConfEntry.setDescription(description);
                }
            }
            super.update(centralConfEntry, true);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }

    public Object getConf(String key) throws DaoException {
        try {
            return super.findOneByParams(new String[]{"key"}, new Object[]{key}, CentralConfEntry.class);
        } catch (MongoDaoException e) {
            throw new DaoException(e);
        }
    }
}
