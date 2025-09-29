package com.enonic.xp.core.impl.content.serializer;


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterators;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;

final class LayoutComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<LayoutComponent>
{
    private final RegionDataSerializer regionDataSerializer;

    LayoutComponentDataSerializer( final RegionDataSerializer regionDataSerializer )
    {
        this.regionDataSerializer = regionDataSerializer;
    }

    @Override
    public void toData( final LayoutComponent component, final PropertySet parent )
    {
        super.toData( component, parent );

        if ( component.hasRegions() )
        {
            for ( final Region region : component.getRegions() )
            {
                regionDataSerializer.toData( region, parent );
            }
        }
    }

    @Override
    public LayoutComponent fromData( final PropertySet data )
    {
        return fromData( data, new ArrayList<>() );
    }

    public LayoutComponent fromData( final PropertySet layoutData, final List<PropertySet> componentsAsData )
    {
        final PropertySet layoutDataSet = layoutData.getSet( LayoutComponentType.INSTANCE.toString() );
        final LayoutComponent.Builder layoutBuilder = LayoutComponent.create();

        if ( layoutDataSet != null && layoutDataSet.isNotNull( DESCRIPTOR ) )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( layoutDataSet.getString( DESCRIPTOR ) );

            layoutBuilder
                .descriptor( descriptorKey )
                .config( getConfigFromData( layoutDataSet, descriptorKey ) )
                .regions( buildRegions( layoutData.getString( PATH ), componentsAsData ) );
        }

        return layoutBuilder.build();
    }

    private Regions buildRegions( final String layoutPath, final List<PropertySet> componentsAsData )
    {
        final Regions.Builder regionsBuilder = Regions.create();

        getRegionDescriptors( layoutPath, componentsAsData ).forEach( regionDescriptor -> {
            regionsBuilder.add( regionDataSerializer.fromData( regionDescriptor, layoutPath, componentsAsData ) );
        } );

        return regionsBuilder.build();
    }

    private RegionDescriptors getRegionDescriptors( final String layoutPath, final List<PropertySet> componentsAsData )
    {
        final int childrenLevel = Iterators.size( ComponentPath.from( layoutPath ).iterator() ) + 1;
        return regionDataSerializer.getRegionDescriptorsAtLevel( childrenLevel, componentsAsData );
    }
}
