package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;

public final class RegionMapper
{
    public static void serialize( final MapGenerator gen, final Region value )
    {
        gen.value( "name", value.getName() );
        serializeComponents( gen, value.getComponents() );
    }

    private static void serializeComponents( final MapGenerator gen, final Iterable<PageComponent> values )
    {
        gen.array( "components" );
        for ( final PageComponent component : values )
        {
            gen.map();
            PageComponentMapper.serialize( gen, component );
            gen.end();
        }
        gen.end();
    }
}
