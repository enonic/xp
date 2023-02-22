package com.enonic.xp.region;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ComponentPath
    implements Iterable<ComponentPath.RegionAndComponent>
{
    public static final String DIVIDER = "/";

    private final ImmutableList<RegionAndComponent> regionAndComponentList;

    public ComponentPath( final ImmutableList<RegionAndComponent> regionAndComponentList )
    {
        this.regionAndComponentList = regionAndComponentList;
    }

    public static ComponentPath from( final RegionPath parentPath, final int componentIndex )
    {
        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        if ( parentPath.getParentComponentPath() != null )
        {
            builder.addAll( parentPath.getParentComponentPath().regionAndComponentList );
        }
        builder.add( RegionAndComponent.from( parentPath.getRegionName(), componentIndex ) );
        return new ComponentPath( builder.build() );
    }

    public static ComponentPath from( final String str )
    {
        final List<String> valueList = Splitter.on( DIVIDER ).omitEmptyStrings().splitToList( str );

        Preconditions.checkArgument( valueList.size() % 2 == 0, "Expected even number of path elements: " + str );

        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        for ( int i = 0; i < valueList.size() - 1; i += 2 )
        {
            final RegionName regionName = RegionName.fromString( valueList.get( i ) );
            final ComponentIndex componentIndex = ComponentIndex.fromString( valueList.get( i + 1 ) );
            builder.add( new RegionAndComponent( regionName, componentIndex ) );

        }
        return new ComponentPath( builder.build() );
    }

    public int getComponentIndex()
    {
        return getLastLevel().getComponentIndex();
    }

    public boolean isEmpty()
    {
        return this.regionAndComponentList.isEmpty();
    }

    int numberOfLevels()
    {
        return this.regionAndComponentList.size();
    }

    RegionAndComponent getFirstLevel()
    {
        return this.regionAndComponentList.get( 0 );
    }

    RegionAndComponent getLastLevel()
    {
        return this.regionAndComponentList.get( regionAndComponentList.size() - 1 );
    }

    ComponentPath removeFirstLevel()
    {
        if ( this.numberOfLevels() <= 1 )
        {
            return null;
        }

        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        for ( int i = 1; i < this.regionAndComponentList.size(); i++ )
        {
            builder.add( this.regionAndComponentList.get( i ) );
        }
        return new ComponentPath( builder.build() );
    }

    @Override
    public Iterator<RegionAndComponent> iterator()
    {
        return regionAndComponentList.iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ComponentPath ) )
        {
            return false;
        }
        final ComponentPath that = (ComponentPath) o;
        return regionAndComponentList.equals( that.regionAndComponentList );
    }

    @Override
    public int hashCode()
    {
        return regionAndComponentList.hashCode();
    }

    @Override
    public String toString()
    {
        return this.regionAndComponentList.stream().map( Objects::toString ).collect( Collectors.joining( DIVIDER, DIVIDER, "" ) );
    }

    public static class RegionAndComponent
    {
        private static final String DIVIDER = "/";

        private final RegionName regionName;

        private final ComponentIndex componentIndex;

        public RegionAndComponent( final RegionName regionName, final ComponentIndex componentIndex )
        {
            this.regionName = regionName;
            this.componentIndex = componentIndex;
        }

        public static RegionAndComponent from( final String regionName, final int componentIndex )
        {
            return new RegionAndComponent( new RegionName( regionName ), new ComponentIndex( componentIndex ) );
        }

        public static RegionAndComponent from( final String str )
        {
            final Iterable<String> values = Splitter.on( DIVIDER ).omitEmptyStrings().split( str );
            final Iterator<String> iterator = values.iterator();
            final RegionName regionName = RegionName.fromString( iterator.next() );
            final ComponentIndex componentIndex = ComponentIndex.fromString( iterator.next() );
            return new RegionAndComponent( regionName, componentIndex );
        }

        public String getRegionName()
        {
            return regionName.name;
        }

        public int getComponentIndex()
        {
            return componentIndex.index;
        }

        @Override
        public String toString()
        {
            return this.regionName + DIVIDER + this.componentIndex;
        }
    }

    public static class RegionName
    {
        private final String name;

        public RegionName( final String name )
        {
            this.name = name;
        }

        public static RegionName fromString( final String str )
        {
            return new RegionName( str );
        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }

    public static class ComponentIndex
    {
        private final int index;

        public ComponentIndex( final int index )
        {
            this.index = index;
        }

        public static ComponentIndex fromString( final String str )
        {
            return new ComponentIndex( Integer.parseInt( str ) );
        }

        @Override
        public String toString()
        {
            return Integer.toString( this.index );
        }
    }
}
