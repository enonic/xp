package com.enonic.xp.core.impl.app.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;

public final class ApplicationConfigMap
{
    public final static ApplicationConfigMap INSTANCE = new ApplicationConfigMap();

    private final Map<ApplicationKey, Configuration> map;

    private ApplicationConfigMap()
    {
        this.map = new ConcurrentHashMap<>();
    }

    public Configuration get( final ApplicationKey key )
    {
        return this.map.get( key );
    }

    public void put( final ApplicationKey key, final Configuration config )
    {
        this.map.put( key, config );
    }

    public void remove( final ApplicationKey key )
    {
        this.map.remove( key );
    }
}
