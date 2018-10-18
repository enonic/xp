package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Region;

public class RegionDataSerializer
    extends AbstractDataSetSerializer<Region, Region>
{
    private final ComponentsDataSerializer componentsSerializer = new ComponentsDataSerializer();

    @Override
    public void toData( final Region region, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( ComponentDataSerializer.COMPONENTS );

        asData.addString( ComponentDataSerializer.NAME, region.getName() );
        asData.addString( ComponentDataSerializer.PATH, region.getRegionPath().toString() );
        asData.addString( ComponentDataSerializer.TYPE, Region.class.getSimpleName() );

        componentsSerializer.toData( region.getComponents(), parent );
    }

    public Region fromData( final SerializedData data )
    {
        final Region.Builder region = Region.create().name( data.getAsData().getString( ComponentDataSerializer.NAME ) );

        for ( Component component : componentsSerializer.fromData( data ) )
        {
            region.add( component );
        }

        return region.build();
    }
}
