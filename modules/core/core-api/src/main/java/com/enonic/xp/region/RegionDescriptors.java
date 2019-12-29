package com.enonic.xp.region;


import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RegionDescriptors
    implements Iterable<RegionDescriptor>
{
    private final ImmutableMap<String, RegionDescriptor> regionByName;

    public RegionDescriptors( final Builder builder )
    {
        this.regionByName = builder.regionsByName.build();
    }

    public RegionDescriptor getRegionDescriptor( final String name )
    {
        return regionByName.get( name );
    }

    public int numberOfRegions()
    {
        return this.regionByName.size();
    }

    @Override
    public Iterator<RegionDescriptor> iterator()
    {
        return regionByName.values().iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableMap.Builder<String, RegionDescriptor> regionsByName = new ImmutableMap.Builder<>();

        public Builder add( final RegionDescriptor value )
        {
            regionsByName.put( value.getName(), value );
            return this;
        }

        public RegionDescriptors build()
        {
            return new RegionDescriptors( this );
        }
    }
}
