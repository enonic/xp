package com.enonic.xp.core.impl.content.serializer;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;

import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.PATH;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.TYPE;

final class RegionDataSerializer
{
    private final ComponentDataSerializerProvider componentDataSerializerProvider;

    RegionDataSerializer( final ComponentDataSerializerProvider componentDataSerializerProvider )
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

    public Region fromData( final RegionDescriptor regionDescriptor, final PropertyPath parentPath, final List<PropertySet> componentsAsData )
    {
        final Region.Builder region = Region.create().name( regionDescriptor.getName() );
        final PropertyPath regionPath = PropertyPath.from( parentPath, regionDescriptor.getName() );

        for ( final Component component : getComponents( regionPath, componentsAsData ) )
        {
            region.add( component );
        }

        return region.build();
    }

    private List<Component> getComponents( final PropertyPath regionPath, final List<PropertySet> componentsAsData )
    {
        final List<Component> componentList = new ArrayList<>();

        for ( final PropertySet childComponentData : getRegionChildren( regionPath, componentsAsData ) )
        {
            componentList.add( getComponent( childComponentData, componentsAsData ) );
        }

        return componentList;
    }

    private List<PropertySet> getRegionChildren( final PropertyPath regionPath, final List<PropertySet> componentsAsData )
    {
        return componentsAsData.stream().filter( item -> isItemChildOf( item, regionPath ) ).collect( Collectors.toList() );
    }

    private boolean isItemChildOf( final PropertySet item, final PropertyPath parentPath )
    {
        final PropertyPath itemPropertyPath = getPath( item );

        return itemPropertyPath.elementCount() > 1 && itemPropertyPath.getParent().equals( parentPath );
    }

    PropertyPath getPath( final PropertySet item )
    {
        final String itemPath = item.getString( PATH );

        if (itemPath == ComponentPath.DIVIDER) {
            return PropertyPath.ROOT;
        }

        final String processedItemPath = itemPath.substring( 1 ).replaceAll( ComponentPath.DIVIDER, PropertyPath.ELEMENT_DIVIDER );
        return PropertyPath.from( processedItemPath );
    }


    Component getComponent( final PropertySet componentData, final List<PropertySet> componentsAsData )
    {
        final ComponentType type = ComponentTypes.byShortName( componentData.getString( TYPE ) );
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
        final PropertyPath parentPath = getPath( item );

        return componentsAsData.stream().filter( componentAsData -> isItemDescendantOf( componentAsData, parentPath ) ).collect(
            Collectors.toList() );
    }

    private boolean isItemDescendantOf( final PropertySet item, final PropertyPath parentPropertyPath )
    {
        return getPath( item ).startsWith( parentPropertyPath );
    }
}
