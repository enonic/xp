package com.enonic.xp.region;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Beta
public final class ComponentPath
    implements Iterable<ComponentPath.RegionAndComponent>
{
    private static final String DIVIDER = "/";

    private final ImmutableList<RegionAndComponent> regionAndComponentList;

    private final String refString;

    public ComponentPath( final ImmutableList<RegionAndComponent> regionAndComponentList )
    {
        this.regionAndComponentList = regionAndComponentList;
        this.refString = toString( this );
    }

    public static ComponentPath from( final RegionPath parentPath, final int componentIndex )
    {
        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        if ( parentPath.getParentComponentPath() != null )
        {
            for ( final RegionAndComponent regionAndComponent : parentPath.getParentComponentPath() )
            {
                builder.add( regionAndComponent );
            }
        }
        builder.add( RegionAndComponent.from( parentPath.getRegionName(), componentIndex ) );
        return new ComponentPath( builder.build() );
    }

    public static ComponentPath from( final String str )
    {
        final Iterable<String> values = Splitter.on( DIVIDER ).omitEmptyStrings().split( str );
        final List<String> valueList = Lists.newArrayList( values );

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
        return ( o instanceof ComponentPath ) && ( (ComponentPath) o ).refString.equals( this.refString );
    }

    private String toString( final ComponentPath componentPath )
    {
        return Joiner.on( DIVIDER ).join( componentPath.regionAndComponentList );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static class RegionAndComponent
    {
        private static final String DIVIDER = "/";

        private final RegionName regionName;

        private final ComponentIndex componentIndex;

        private final String refString;

        public RegionAndComponent( final RegionName regionName, final ComponentIndex componentIndex )
        {
            this.regionName = regionName;
            this.componentIndex = componentIndex;
            this.refString = toString( this );
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

        private String toString( final RegionAndComponent regionAndComponent )
        {
            return regionAndComponent.regionName + DIVIDER + regionAndComponent.componentIndex;
        }

        @Override
        public String toString()
        {
            return this.refString;
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

        private final String refString;

        public ComponentIndex( final int index )
        {
            this.index = index;
            this.refString = String.valueOf( this.index );
        }

        public static ComponentIndex fromString( final String str )
        {
            return new ComponentIndex( Integer.valueOf( str ) );
        }

        @Override
        public String toString()
        {
            return this.refString;
        }
    }
}
