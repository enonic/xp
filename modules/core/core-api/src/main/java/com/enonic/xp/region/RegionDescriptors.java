package com.enonic.xp.region;


import java.util.Iterator;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RegionDescriptors
    implements Iterable<RegionDescriptor>
{
    private final ImmutableSet<RegionDescriptor> regionDescriptors;

    private RegionDescriptors( final ImmutableSet<RegionDescriptor> regionDescriptors )
    {
        this.regionDescriptors = regionDescriptors;
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
