package com.enonic.xp.testing.mock;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.script.impl.service.ServiceRef;
import com.enonic.xp.script.impl.service.ServiceRegistry;

public final class MockServiceRegistry
    implements ServiceRegistry
{
    private final Map<String, ServiceRef> map;

    public MockServiceRegistry()
    {
        this.map = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ServiceRef<T> getService( final Class<T> type )
    {
        return getService( type, null );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ServiceRef<T> getService( final Class<T> type, final String filter )
    {
        final ServiceRef ref = this.map.get( getRegistryKey( type, filter ) );
        if ( ref != null )
        {
            return (ServiceRef<T>) ref;
        }

        return () -> {
            throw new IllegalArgumentException( "Service [" + type.getName() + "] not found" );
        };
    }

    public <T> void register( final Class<T> type, final String filter, final T instance )
    {
        final ServiceRef<T> ref = toServiceRef( instance );
        this.map.put( getRegistryKey( type, filter ), ref );
    }

    private <T> String getRegistryKey( final Class<T> type, final String filter )
    {
        return filter == null ? type.getName() : type.getName() + ":" + filter;
    }

    private <T> ServiceRef<T> toServiceRef( final T instance )
    {
        return () -> instance;
    }
}
