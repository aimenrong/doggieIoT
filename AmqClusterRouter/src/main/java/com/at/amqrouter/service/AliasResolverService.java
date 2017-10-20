package com.at.amqrouter.service;

/**
 * Created by eyonlig on 9/27/2017.
 */
public interface AliasResolverService {
    Object resolve(String input, String...args);
}
