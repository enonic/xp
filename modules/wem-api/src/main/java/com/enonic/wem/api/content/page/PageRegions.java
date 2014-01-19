package com.enonic.wem.api.content.page;


import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.page.region.Region;

public final class PageRegions
    implements Iterable<Region>
{
    private final ImmutableMap<String, Region> regionByName;

    private PageRegions( final Builder builder )
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

    public static Builder newPageRegions()
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

        public PageRegions build()
        {
            return new PageRegions( this );
        }
    }
}


