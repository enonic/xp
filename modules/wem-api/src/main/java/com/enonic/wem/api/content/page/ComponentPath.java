package com.enonic.wem.api.content.page;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * {@code
 * _/component/<region-name>/<component-name>
 * }
 * <p/>
 * {@code
 * _/component/<region-name>/<layout-component-name>/<region-name>/<component-name>
 * }
 */
public final class ComponentPath
    implements Iterable<ComponentPath.RegionAndComponent>
{
    private static final String DIVIDER = "/";

    private final ImmutableList<RegionAndComponent> regionAndComponentList;

    private final String refString;

    public static ComponentPath from( final RegionAndComponent... regionAndComponents )
    {
        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        for ( final RegionAndComponent regionAndComponent : regionAndComponents )
        {
            builder.add( regionAndComponent );
        }
        return new ComponentPath( builder.build() );
    }

    public static ComponentPath from( final ComponentPath parentPath, final RegionAndComponent child )
    {
        final ImmutableList.Builder<RegionAndComponent> builder = new ImmutableList.Builder<>();
        for ( final RegionAndComponent parent : parentPath )
        {
            builder.add( parent );
        }
        builder.add( child );
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
            final String regionName = valueList.get( i );
            final ComponentName componentName = new ComponentName( valueList.get( i + 1 ) );
            builder.add( new RegionAndComponent( regionName, componentName ) );

        }
        return new ComponentPath( builder.build() );
    }

    public ComponentPath( final ImmutableList<RegionAndComponent> regionAndComponentList )
    {
        this.regionAndComponentList = regionAndComponentList;
        this.refString = toString( this );
    }

    public int numberOfLevels()
    {
        return this.regionAndComponentList.size();
    }

    public RegionAndComponent getFirstLevel()
    {
        return this.regionAndComponentList.get( 0 );
    }

    public ComponentPath removeFirstLevel()
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

        private final String regionName;

        private final ComponentName componentName;

        private final String refString;

        public static RegionAndComponent from( final String regionName, final ComponentName componentName )
        {
            return new RegionAndComponent( regionName, componentName );
        }

        public static RegionAndComponent from( final String str )
        {
            final Iterable<String> values = Splitter.on( DIVIDER ).omitEmptyStrings().split( str );
            final Iterator<String> iterator = values.iterator();
            final String regionName = iterator.next();
            final ComponentName componentName = new ComponentName( iterator.next() );
            return new RegionAndComponent( regionName, componentName );
        }

        public RegionAndComponent( final String regionName, final ComponentName componentName )
        {
            this.regionName = regionName;
            this.componentName = componentName;
            this.refString = toString( this );
        }

        public String getRegionName()
        {
            return regionName;
        }

        public ComponentName getComponentName()
        {
            return componentName;
        }

        private String toString( final RegionAndComponent regionAndComponent )
        {
            return regionAndComponent.regionName + DIVIDER + regionAndComponent.componentName;
        }

        public String toString()
        {
            return this.refString;
        }
    }
}
