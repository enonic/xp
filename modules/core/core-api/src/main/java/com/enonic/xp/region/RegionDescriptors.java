package com.enonic.xp.region;


import java.util.Iterator;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RegionDescriptors
    implements Iterable<RegionDescriptor>
{
    private final ImmutableSet<RegionDescriptor> regionDescriptors;

    public RegionDescriptors( final ImmutableSet<RegionDescriptor> regionDescriptors )
    {
        this.regionDescriptors = regionDescriptors;
    }

    @Deprecated
    public RegionDescriptor getRegionDescriptor( final String name )
    {
        return regionDescriptors.stream().filter( regionDescriptor -> regionDescriptor.getName().equals( name ) ).findAny().orElse( null );
    }

    public int numberOfRegions()
    {
        return this.regionDescriptors.size();
    }

    @Override
    public Iterator<RegionDescriptor> iterator()
    {
        return regionDescriptors.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<RegionDescriptor> regionsDescriptors = new ImmutableSet.Builder<>();

        public Builder add( final RegionDescriptor value )
        {
            regionsDescriptors.add( value );
            return this;
        }

        public RegionDescriptors build()
        {
            return new RegionDescriptors( regionsDescriptors.build() );
        }
    }
}
