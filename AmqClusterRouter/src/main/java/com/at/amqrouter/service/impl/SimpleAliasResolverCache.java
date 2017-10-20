package com.at.amqrouter.service.impl;

import com.at.amqrouter.service.AliasResolverCacheService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Service("SimpleAliasResolverCache")
public class SimpleAliasResolverCache implements AliasResolverCacheService {

    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    public Object lookup(String key, String... criteria) {
        if (!validateArgus(key, criteria)) {
            throw new IllegalArgumentException("Argument is empty");
        }
        return cache.get(formatNewKey(key, criteria));
    }

    public void store(String key, Object value, String... criteria) {
        if (!validateArgus(key, value, criteria)) {
            throw new IllegalArgumentException("Argument is empty");
        }
        cache.put(formatNewKey(key, criteria), value);
    }

    private String formatNewKey(String key, String...criteria) {
        StringBuffer sb = new StringBuffer(key);
        if (criteria != null) {
            for (String item : criteria) {
                sb.append("::" + item);
            }
        }
        return sb.toString();
    }

    private boolean validateArgus(String key, String...critera) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        if (null != critera) {
            for (String item : critera) {
                if (StringUtils.isEmpty(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateArgus(String key, Object value, String...critera) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return false;
        }
        return true;
    }
}
