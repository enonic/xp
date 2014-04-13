package com.enonic.wem.core.resource;

import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceService;

public final class MockResourceService
    implements ResourceService
{
    private final Map<ResourceKey, Resource> map;

    public MockResourceService()
    {
        this.map = Maps.newHashMap();
    }

    @Override
    public boolean hasResource( final ResourceKey key )
    {
        return this.map.get( key ) != null;
    }

    @Override
    public Resource getResource( final ResourceKey key )
        throws ResourceNotFoundException
    {
        final Resource resource = this.map.get( key );
        if ( resource != null )
        {
            return resource;
        }

        throw new ResourceNotFoundException( key );
    }

    @Override
    public ResourceKeys getChildren( final ResourceKey parent )
    {
        return ResourceKeys.from( Collections2.filter( this.map.keySet(), new Predicate<ResourceKey>()
        {
            @Override
            public boolean apply( final ResourceKey input )
            {
                return input.toString().startsWith( parent.toString() );
            }
        } ) );
    }

    public void addResource( final ResourceKey key, final String content )
    {
        final Resource resource = Resource.newResource().stringValue( content ).key( key ).build();
        this.map.put( key, resource );
    }
}
