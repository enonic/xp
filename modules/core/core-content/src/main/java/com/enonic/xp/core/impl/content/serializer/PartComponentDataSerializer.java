package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;

final class PartComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<PartComponent>
{
    private static final String DEFAULT_NAME = "Part";

    private final PartDescriptorService partDescriptorService;

    public PartComponentDataSerializer( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Override
    public PartComponent fromData( final PropertySet data )
    {
        PartComponent.Builder component = PartComponent.create().name( DEFAULT_NAME );

        final PropertySet specialBlockSet = data.getSet( PartComponentType.INSTANCE.toString() );

        if ( specialBlockSet != null && specialBlockSet.isNotNull( DESCRIPTOR ) )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( specialBlockSet.getString( DESCRIPTOR ) );

            component.descriptor( descriptorKey );
            component.config( getConfigFromData( specialBlockSet, descriptorKey ) );

            final PartDescriptor partDescriptor = partDescriptorService.getByKey( descriptorKey );

            component.name( partDescriptor.getDisplayName() );
        }

        return component.build();
    }
}
