package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.support.serializer.AbstractDataSetSerializer;

import static com.enonic.xp.content.page.region.Region.newRegion;

public class RegionDataSerializer
    extends AbstractDataSetSerializer<Region, Region>
{
    private static final String NAME = "name";

    private final String propertyName;

    private final ComponentsDataSerializer componentsSerializer = new ComponentsDataSerializer();

    public RegionDataSerializer()
    {
        this.propertyName = "region";
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void toData( final Region region, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( propertyName );
        asData.addString( "name", region.getName() );

        componentsSerializer.toData( region.getComponents(), asData );
    }

    public Region fromData( final PropertySet asData )
    {
        final Region.Builder region = newRegion();
        region.name( asData.getString( NAME ) );

        for ( Component component : componentsSerializer.fromData( asData.getProperties( "component" ) ) )
        {
            region.add( component );
        }

        return region.build();
    }
}
