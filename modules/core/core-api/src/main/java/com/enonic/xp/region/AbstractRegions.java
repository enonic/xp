package com.enonic.xp.region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

@Beta
public abstract class AbstractRegions
    implements Iterable<Region>
{
    private final ImmutableList<Region> regions;

    protected AbstractRegions( final Builder builder )
    {
        this.regions = ImmutableList.copyOf( builder.regions );
    }

    public boolean isEmpty()
    {
        return regions.isEmpty();
    }

    public Region getRegion( final String name )
    {
        for ( final Region region : this.regions )
        {
            if ( region.getName().equals( name ) )
            {
                return region;
            }
        }
        return null;
    }

    public Component getComponent( final ComponentPath path )
    {
        Preconditions.checkNotNull( path, "no path for Component given" );
        Preconditions.checkArgument( path.numberOfLevels() > 0, "empty path for Component given" );

        final ComponentPath.RegionAndComponent first = path.getFirstLevel();
        final Region region = getRegion( first.getRegionName() );
        final Component component = region.getComponent( first.getComponentIndex() );

        if ( path.numberOfLevels() == 1 )
        {
            return component;
        }
        else
        {
            if ( !( component instanceof LayoutComponent ) )
            {
                throw new IllegalArgumentException( "Expected component to be a LayoutComponent: " + component.getClass().getSimpleName() );
            }

            final LayoutComponent layoutComponent = (LayoutComponent) component;
            return layoutComponent.getComponent( path.removeFirstLevel() );
        }
    }

    @Override
    public Iterator<Region> iterator()
    {
        return this.regions.iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AbstractRegions other = (AbstractRegions) o;
        return regions.equals( other.regions );
    }

    @Override
    public String toString()
    {
        return this.regions.toString();
    }

    @Override
    public int hashCode()
    {
        return regions.hashCode();
    }

    public abstract AbstractRegions copy();

    public static class Builder<BUILDER extends Builder>
    {
        private final List<Region> regions = new ArrayList<>();

        protected Builder()
        {
            // Default
        }

        protected Builder( final AbstractRegions source )
        {
            for ( final Region sourceRegion : source )
            {
                regions.add( sourceRegion.copy() );
            }
        }

        @SuppressWarnings("unchecked")
        private BUILDER getThis()
        {
            return (BUILDER) this;
        }

        public BUILDER add( final Region region )
        {
            regions.add( region );
            return getThis();
        }
    }
}


