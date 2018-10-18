package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;

public class LayoutComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<LayoutComponent, LayoutComponent>
{
    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    @Override
    public void toData( final LayoutComponent component, final PropertySet parent )
    {
        final PropertySet asSet = parent.addSet( COMPONENTS );
        applyComponentToData( component, asSet );

        if ( component.hasRegions() )
        {
            for ( final Region region : component.getRegions() )
            {
                regionDataSerializer.toData( region, parent );
            }
        }
    }

    @Override
    public LayoutComponent fromData( final SerializedData data )
    {
        final LayoutComponent.Builder layoutComponent = LayoutComponent.create();
        applyComponentFromData( layoutComponent, data.getAsData() );

        final LayoutRegions.Builder layoutRegionsBuilder = LayoutRegions.create();

        for ( final PropertySet regionAsData : getChildren( data ) )
        {
            layoutRegionsBuilder.add( regionDataSerializer.fromData( new SerializedData( regionAsData, data.getComponentsAsData() ) ) );
        }

        layoutComponent.regions( layoutRegionsBuilder.build() );

        return layoutComponent.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return DescriptorKey.from( s );
    }

}
