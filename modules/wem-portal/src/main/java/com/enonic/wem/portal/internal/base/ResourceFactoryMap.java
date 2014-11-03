package com.enonic.wem.portal.internal.base;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public final class ResourceFactoryMap
{
    private final Map<Class, ResourceFactory> map;

    public ResourceFactoryMap( final List<ResourceFactory> list )
    {
        this.map = Maps.newHashMap();
        for ( final ResourceFactory factory : list )
        {
            this.map.put( factory.getType(), factory );
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ResourceFactory<T> getFactory( final Class<T> type )
    {
        return this.map.get( type );
    }

    public <T> T newResource( final Class<T> type )
    {
        final ResourceFactory<T> factory = getFactory( type );
        return factory != null ? factory.newResource() : null;
    }
}
