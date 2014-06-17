package com.enonic.wem.api.content.page;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.region.RegionPath;

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

    public static ComponentPath from( final RegionPath parentPath, final int pageComponentIndex )
    {
        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        if ( parentPath.getParentComponentPath() != null )
        {
            for ( final RegionAndComponent regionAndComponent : parentPath.getParentComponentPath() )
            {
                builder.add( regionAndComponent );
            }
        }
        builder.add( RegionAndComponent.from( parentPath.getRegionName(), pageComponentIndex ) );
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
            final PageComponentIndex pageComponentIndex = PageComponentIndex.fromString( valueList.get( i + 1 ) );
            builder.add( new RegionAndComponent( regionName, pageComponentIndex ) );

        }
        return new ComponentPath( builder.build() );
    }

    public int getComponentIndex()
    {
        return getLastLevel().getPageComponentIndex();
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

    private String toString( final ComponentPath componentPath )
    {
        return Joiner.on( DIVIDER ).join( componentPath.regionAndComponentList );
    }

    public String toString()
    {
        return refString;
    }

    public static class RegionAndComponent
    {
        private static final String DIVIDER = "/";

        private final RegionName regionName;

        private final PageComponentIndex pageComponentIndex;

        private final String refString;

        public RegionAndComponent( final RegionName regionName, final PageComponentIndex pageComponentIndex )
        {
            this.regionName = regionName;
            this.pageComponentIndex = pageComponentIndex;
            this.refString = toString( this );
        }

        public static RegionAndComponent from( final String regionName, final int pageComponentIndex )
        {
            return new RegionAndComponent( new RegionName( regionName ), new PageComponentIndex( pageComponentIndex ) );
        }

        public static RegionAndComponent from( final String str )
        {
            final Iterable<String> values = Splitter.on( DIVIDER ).omitEmptyStrings().split( str );
            final Iterator<String> iterator = values.iterator();
            final RegionName regionName = RegionName.fromString( iterator.next() );
            final PageComponentIndex pageComponentIndex = PageComponentIndex.fromString( iterator.next() );
            return new RegionAndComponent( regionName, pageComponentIndex );
        }

        public String getRegionName()
        {
            return regionName.name;
        }

        public int getPageComponentIndex()
        {
            return pageComponentIndex.index;
        }

        private String toString( final RegionAndComponent regionAndComponent )
        {
            return regionAndComponent.regionName + DIVIDER + regionAndComponent.pageComponentIndex;
        }

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

        public String toString()
        {
            return this.name;
        }
    }

    public static class PageComponentIndex
    {
        private final int index;

        private final String refString;

        public PageComponentIndex( final int index )
        {
            this.index = index;
            this.refString = String.valueOf( this.index );
        }

        public static PageComponentIndex fromString( final String str )
        {
            return new PageComponentIndex( Integer.valueOf( str ) );
        }

        public String toString()
        {
            return this.refString;
        }
    }
}
