package com.enonic.xp.lib.mapper;

import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Region;

public final class RegionMapper
    implements MapSerializable
{
    private final Region value;

    public RegionMapper( final Region value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final Region value )
    {
        gen.map( value.getName() );
        serializeComponents( gen, value.getComponents() );
        gen.end();
    }

    private void serializeComponents( final MapGenerator gen, final Iterable<Component> values )
    {
        gen.array( "components" );
        for ( final Component component : values )
        {
            gen.map();
            new ComponentMapper( component ).serialize( gen );
            gen.end();
        }
        gen.end();
        gen.value( "name", this.value.getName() );
    }
}
