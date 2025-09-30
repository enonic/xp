package com.enonic.xp.region;

import java.util.Iterator;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Regions
    implements Iterable<Region>
{
    private final ImmutableList<Region> regions;

    private Regions( final Builder builder )
    {
        this.regions = builder.regions.build();
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
        Objects.requireNonNull( path, "no path for Component given" );
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

    public static Builder create()
    {
        return new Builder();
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

        final Regions other = (Regions) o;
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

    public Regions copy()
    {
        return new Regions.Builder( this ).build();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Region> regions = ImmutableList.builder();

        private Builder()
        {
        }

        private Builder( final Regions source )
        {
            for ( final Region sourceRegion : source )
            {
                regions.add( sourceRegion.copy() );
            }
        }

        public Builder add( final Region region )
        {
            regions.add( region );
            return this;
        }

        public Regions build()
        {
            return new Regions( this );
        }
    }
}


