package com.enonic.xp.config;

import java.util.Map;

public interface Configuration
{
    String get( String key );

    boolean exists( String key );

    String getOrDefault( String key, String defValue );

    <T> T get( String key, Class<T> type );

    <T> T getOrDefault( String key, Class<T> type, T defValue );

    Configuration subConfig( String prefix );

    Map<String, String> asMap();
}
