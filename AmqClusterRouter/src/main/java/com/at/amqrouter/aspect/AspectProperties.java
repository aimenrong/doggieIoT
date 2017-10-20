package com.at.amqrouter.aspect;

import java.util.Properties;

/**
 * Created by eyonlig on 9/25/2017.
 */
public class AspectProperties extends Properties {
    @Override
    public synchronized Object put(Object key, Object value) {
        return super.put(key, value);
    }
}
