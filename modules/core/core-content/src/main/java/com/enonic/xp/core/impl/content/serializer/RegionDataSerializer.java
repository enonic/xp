package com.enonic.xp.core.impl.content.serializer;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;

final class RegionDataSerializer
{
    private final ComponentDataSerializerProvider componentDataSerializerProvider;

    public RegionDataSerializer( final ComponentDataSerializerProvider componentDataSerializerProvider )
    {
        this.componentDataSerializerProvider = componentDataSerializerProvider;
    }

    public void toData( final Region region, final PropertySet parent )
    {
        for ( final Component component : region.getComponents() )
        {
            componentDataSerializerProvider.getDataSerializer( component.getType() ).toData( component, parent );
        }
    }

    public Region fromData( final RegionDescriptor regionDescriptor, final String parentPath, final List<PropertySet> componentsAsData )
    {
        final Region.Builder region = Region.create();

        region.name( regionDescriptor.getName() );

        final StringBuilder regionPathBuilder = new StringBuilder( ComponentPath.DIVIDER + regionDescriptor.getName() );

        if ( parentPath != ComponentPath.DIVIDER )
        {
            regionPathBuilder.insert( 0, parentPath );
        }

        for ( final Component component : getComponents( regionPathBuilder.toString(), componentsAsData ) )
        {
            region.add( component );
        }

        return region.build();
    }

    private List<Component> getComponents( final String regionPath, final List<PropertySet> componentsAsData )
    {
        final List<Component> componentList = new ArrayList<>();

        for ( final PropertySet childComponentData : getRegionChildren( regionPath, componentsAsData ) )
        {
            componentList.add( getComponent( childComponentData, componentsAsData ) );
        }

        return componentList;
    }

    private List<PropertySet> getRegionChildren( final String regionPath, final List<PropertySet> componentsAsData )
    {
        return componentsAsData.stream().filter( item -> isItemChildOf( item, regionPath ) ).collect( Collectors.toList() );
    }

    private boolean isItemChildOf( final PropertySet item, final String parentPath )
    {
        final String itemPath = item.getString( ComponentDataSerializer.PATH );

        return itemPath.startsWith( parentPath ) && ( getLevel( itemPath ) - getLevel( parentPath ) == 1 );
    }

    private int getLevel( final String path )
    {
        return path.split( ComponentPath.DIVIDER ).length;
    }

    Component getComponent( final PropertySet componentData, final List<PropertySet> componentsAsData )
    {
        final ComponentType type = ComponentTypes.byShortName( componentData.getString( ComponentDataSerializer.TYPE ) );
        final ComponentDataSerializer componentDataSerializer = componentDataSerializerProvider.getDataSerializer( type );

        if ( componentDataSerializer instanceof LayoutComponentDataSerializer )
        {
            return ( (LayoutComponentDataSerializer) componentDataSerializer ).fromData( componentData, getDescendantsOf( componentData,
                                                                                                                          componentsAsData ) );
        }

        return componentDataSerializer.fromData( componentData );
    }

    private List<PropertySet> getDescendantsOf( final PropertySet item, final List<PropertySet> componentsAsData )
    {
        final String parentPath = item.getString( ComponentDataSerializer.PATH );

        return componentsAsData.stream().filter( componentAsData -> isItemDescendantOf( componentAsData, parentPath ) ).collect(
            Collectors.toList() );
    }

    private boolean isItemDescendantOf( final PropertySet item, final String parentPath )
    {
        final String itemPath = item.getString( ComponentDataSerializer.PATH );

        return itemPath.startsWith( parentPath );
    }
}
