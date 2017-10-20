package com.at.amqrouter.service;

/**
 * Created by eyonlig on 9/27/2017.
 */
public interface AliasResolverCacheService {
    Object lookup(String key, String...criteria);
    void store(String key, Object value, String...criteria);
}
