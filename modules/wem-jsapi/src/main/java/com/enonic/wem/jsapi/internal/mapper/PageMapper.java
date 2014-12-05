package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;

public final class PageMapper
{
    public static void serialize( final MapGenerator gen, final Page value )
    {
        gen.value( "template", value.getTemplate() );
        gen.value( "controller", value.getController() );

        gen.map( "config" );
        PropertyTreeMapper.serialize( gen, value.getConfig() );
        gen.end();

        serializeRegions( gen, value.getRegions() );
    }

    private static void serializeRegions( final MapGenerator gen, final PageRegions values )
    {
        gen.array( "regions" );
        for ( final Region region : values )
        {
            gen.map();
            RegionMapper.serialize( gen, region );
            gen.end();
        }
        gen.end();
    }
}
