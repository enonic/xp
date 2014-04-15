package com.enonic.wem.core.resource;

import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;

public final class MockResourceService
    extends AbstractResourceService
{
    private final Map<ResourceKey, Resource> map;

    public MockResourceService()
    {
        this.map = Maps.newHashMap();
    }

    @Override
    protected Resource resolve( final ResourceKey key )
    {
        return this.map.get( key );
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
