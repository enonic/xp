package com.enonic.wem.api.content.page;


import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.region.Region;

public abstract class AbstractRegions
    implements Iterable<Region>
{
    private final ImmutableMap<String, Region> regionByName;

    protected AbstractRegions( final Builder builder )
    {
        this.regionByName = builder.regions.build();
    }

    public Region getRegion( final String name )
    {
        return this.regionByName.get( name );
    }

    public PageComponent getComponent( final ComponentName name )
    {
        for ( Region region : this )
        {
            for ( PageComponent component : region.getComponents() )
            {
                if ( name.equals( component.getName() ) )
                {
                    return component;
                }
                else if ( component instanceof LayoutComponent )
                {
                    final LayoutComponent layoutComponent = (LayoutComponent) component;
                    if ( layoutComponent.hasRegions() )
                    {
                        final PageComponent match = layoutComponent.getComponent( name );
                        if ( match != null )
                        {
                            return match;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<Region> iterator()
    {
        return this.regionByName.values().iterator();
    }

    public static class Builder<BUILDER extends Builder>
    {
        private ImmutableMap.Builder<String, Region> regions = new ImmutableMap.Builder<>();

        @SuppressWarnings("unchecked")
        private BUILDER getThis()
        {
            return (BUILDER) this;
        }

        public BUILDER add( final Region region )
        {
            regions.put( region.getName(), region );
            return getThis();
        }
    }
}


