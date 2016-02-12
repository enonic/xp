package com.enonic.xp.region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

@Beta
public final class Regions
    implements Iterable<Region>
{
    private final ImmutableList<Region> regions;

    private Regions( final Builder builder )
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

    public Regions replace( final ComponentPath path, final Component component )
    {
        final ComponentPath.RegionAndComponent regionCmp = path.getFirstLevel();
        final Region region = getRegion( regionCmp.getRegionName() );
        if ( region == null )
        {
            return this.copy();
        }

        final int componentIndex = regionCmp.getComponentIndex();
        final Component existingCmp = region.getComponent( componentIndex );
        if ( existingCmp == null )
        {
            return this.copy();
        }

        if ( path.numberOfLevels() == 1 )
        {
            final Region updatedRegion = Region.create( region ).set( componentIndex, component ).build();
            Regions.Builder regions = Regions.create( this );

            final int idx = regions.regions.indexOf( region );
            if ( idx > -1 )
            {
                regions.regions.set( idx, updatedRegion );
            }
            return regions.build();
        }
        else
        {
            if ( !( existingCmp instanceof LayoutComponent ) )
            {
                return this.copy();
            }
            final LayoutComponent layoutComponent = (LayoutComponent) existingCmp;
            final Regions layoutRegions = layoutComponent.getRegions().replace( path.removeFirstLevel(), component );
            final LayoutComponent updatedLayout = LayoutComponent.create( layoutComponent ).regions( layoutRegions ).build();

            final Region updatedRegion = Region.create( region ).set( componentIndex, updatedLayout ).build();
            Regions.Builder regions = Regions.create( this );

            final int idx = regions.regions.indexOf( region );
            if ( idx > -1 )
            {
                regions.regions.set( idx, updatedRegion );
            }
            return regions.build();
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

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Regions source )
    {
        return new Builder( source );
    }

    public Regions copy()
    {
        return Regions.create( this ).build();
    }

    public static class Builder
    {
        private final List<Region> regions = new ArrayList<>();

        private Builder()
        {
            // Default
        }

        private Builder( final Regions source )
        {
            for ( final Region sourceRegion : source.regions )
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


