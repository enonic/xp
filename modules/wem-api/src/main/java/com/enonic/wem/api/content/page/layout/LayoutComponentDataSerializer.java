package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponentDataSerializer;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionDataSerializer;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;

public class LayoutComponentDataSerializer
    extends AbstractDescriptorBasedPageComponentDataSerializer<LayoutComponent, LayoutComponent>
{
    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    public void toData( final LayoutComponent component, final PropertySet parent )
    {
        final PropertySet asSet = parent.addSet( LayoutComponent.class.getSimpleName() );
        applyPageComponentToData( component, asSet );
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
        applyPageComponentFromData( component, asData );

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
        return LayoutDescriptorKey.from( s );
    }

}
