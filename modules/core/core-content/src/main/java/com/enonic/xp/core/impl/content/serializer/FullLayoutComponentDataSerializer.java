package com.enonic.xp.core.impl.content.serializer;


import java.util.List;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;

class FullLayoutComponentDataSerializer
    extends LayoutComponentDataSerializer
{
    private final LayoutDescriptorService layoutDescriptorService;

    FullLayoutComponentDataSerializer( final LayoutDescriptorService layoutDescriptorService, final RegionDataSerializer regionDataSerializer )
    {
        super( regionDataSerializer );
        this.layoutDescriptorService = layoutDescriptorService;
    }

    public LayoutComponent fromData( final PropertySet layoutData, final List<PropertySet> componentsAsData )
    {
        final LayoutComponent.Builder layoutComponent = LayoutComponent.create();

        final LayoutRegions.Builder layoutRegionsBuilder = LayoutRegions.create();

        final PropertySet specialBlockSet = layoutData.getSet( LayoutComponentType.INSTANCE.toString() );

        if ( specialBlockSet != null && specialBlockSet.isNotNull( DESCRIPTOR ) )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( specialBlockSet.getString( DESCRIPTOR ) );

            layoutComponent.descriptor( descriptorKey );
            layoutComponent.config( getConfigFromData( specialBlockSet, descriptorKey ) );

            final LayoutDescriptor layoutDescriptor = layoutDescriptorService.getByKey( descriptorKey );

            final String layoutPath = layoutData.getString( PATH );

            if ( layoutDescriptor.getRegions() != null && layoutDescriptor.getRegions().numberOfRegions() > 0 )
            {
                layoutDescriptor.getRegions().forEach( regionDescriptor -> {
                    layoutRegionsBuilder.add( regionDataSerializer.fromData( regionDescriptor, layoutPath, componentsAsData ) );
                } );
            }
        }

        layoutComponent.regions( layoutRegionsBuilder.build() );

        return layoutComponent.build();
    }
}
