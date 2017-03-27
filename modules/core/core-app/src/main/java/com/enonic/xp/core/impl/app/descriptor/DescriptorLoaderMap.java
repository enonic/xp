package com.enonic.xp.core.impl.app.descriptor;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorLoader;

final class DescriptorLoaderMap
{
    private final Map<Class, DescriptorLoader> map;

    DescriptorFacetFactory facetFactory;

    DescriptorLoaderMap()
    {
        this.map = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    private <T extends Descriptor> DescriptorLoader<T> get( final Class<T> type )
    {
        return this.map.get( type );
    }

    @SuppressWarnings("unchecked")
    void add( final DescriptorLoader loader )
    {
        this.map.put( loader.getType(), loader );
    }

    void remove( final DescriptorLoader loader )
    {
        this.map.remove( loader.getType() );
    }

    <T extends Descriptor> DescriptorFacet<T> facet( final Class<T> type )
    {
        final DescriptorLoader<T> loader = get( type );
        if ( ( this.facetFactory == null ) || ( loader == null ) )
        {
            return new NopDescriptorFacet<>();
        }

        return this.facetFactory.create( loader );
    }
}
