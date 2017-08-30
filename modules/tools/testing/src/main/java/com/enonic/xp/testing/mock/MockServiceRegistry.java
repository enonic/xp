package com.enonic.xp.testing.mock;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.script.impl.service.ServiceRef;
import com.enonic.xp.script.impl.service.ServiceRegistry;

public final class MockServiceRegistry
    implements ServiceRegistry
{
    private final Map<Class, ServiceRef> map;

    public MockServiceRegistry()
    {
        this.map = Maps.newHashMap();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ServiceRef<T> getService( final Class<T> type )
    {
        final ServiceRef ref = this.map.get( type );
        if ( ref != null )
        {
            return (ServiceRef<T>) ref;
        }

        return () -> {
            throw new IllegalArgumentException( "Service [" + type.getName() + "] not found" );
        };
    }

    public <T> void register( final Class<T> type, final T instance )
    {
        final ServiceRef<T> ref = toServiceRef( instance );
        this.map.put( type, ref );
    }

    private <T> ServiceRef<T> toServiceRef( final T instance )
    {
        return () -> instance;
    }
}
