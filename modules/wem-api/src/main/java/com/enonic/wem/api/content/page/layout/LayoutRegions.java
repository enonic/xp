package com.enonic.wem.api.content.page.layout;


import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.page.region.Region;

public final class LayoutRegions
    implements Iterable<Region>
{
    private final ImmutableMap<String, Region> regionByName;

    private LayoutRegions( final Builder builder )
    {
        this.regionByName = builder.regions.build();
    }

    public Region getRegion( final String name )
    {
        return this.regionByName.get( name );
    }

    @Override
    public Iterator<Region> iterator()
    {
        return this.regionByName.values().iterator();
    }

    public static Builder newLayoutRegions()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableMap.Builder<String, Region> regions = new ImmutableMap.Builder<>();

        public Builder add( final Region region )
        {
            regions.put( region.getName(), region );
            return this;
        }

        public LayoutRegions build()
        {
            return new LayoutRegions( this );
        }
    }
}


