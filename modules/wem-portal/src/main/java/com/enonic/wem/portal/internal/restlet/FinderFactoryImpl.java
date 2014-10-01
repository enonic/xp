package com.enonic.wem.portal.internal.restlet;

import java.util.List;
import java.util.Map;

import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.common.collect.Maps;

public final class FinderFactoryImpl
    implements FinderFactory
{
    private final Map<Class, ResourceFactory> factories;

    public FinderFactoryImpl( final List<ResourceFactory> factories )
    {
        this.factories = Maps.newHashMap();
        for ( final ResourceFactory factory : factories )
        {
            this.factories.put( factory.getType(), factory );
        }
    }

    @Override
    public Finder finder( final Class<? extends ServerResource> type )
    {
        return this.factories.get( type );
    }
}
