package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.Property;
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
        final PropertySet asSet = parent.addSet( LayoutComponent.class.getSimpleName() );
        applyComponentToData( component, asSet );
        if ( component.hasRegions() )
        {
            for ( final Region region : component.getRegions() )
            {
                regionDataSerializer.toData( region, asSet );
            }
        }
    }

    @Override
    public LayoutComponent fromData( final PropertySet asData )
    {
        final LayoutComponent.Builder component = LayoutComponent.create();
        applyComponentFromData( component, asData );

        final LayoutRegions.Builder pageRegionsBuilder = LayoutRegions.create();
        for ( final Property regionAsProp : asData.getProperties( "region" ) )
        {
            pageRegionsBuilder.add( regionDataSerializer.fromData( regionAsProp.getSet() ) );
        }
        component.regions( pageRegionsBuilder.build() );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return DescriptorKey.from( s );
    }

}
