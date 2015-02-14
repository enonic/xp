package com.enonic.xp.core.impl.content.page.region;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.LayoutComponent;
import com.enonic.wem.api.content.page.region.LayoutRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;

public class LayoutComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<LayoutComponent, LayoutComponent>
{
    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

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

    public LayoutComponent fromData( final PropertySet asData )
    {
        final LayoutComponent.Builder component = LayoutComponent.newLayoutComponent();
        applyComponentFromData( component, asData );

        final LayoutRegions.Builder pageRegionsBuilder = LayoutRegions.newLayoutRegions();
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
