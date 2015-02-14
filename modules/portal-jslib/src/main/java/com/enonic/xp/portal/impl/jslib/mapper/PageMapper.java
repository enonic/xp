package com.enonic.xp.portal.impl.jslib.mapper;

import com.enonic.xp.content.page.Page;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class PageMapper
    implements MapSerializable
{
    private final Page value;

    public PageMapper( final Page value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final Page value )
    {
        gen.value( "template", value.getTemplate() );
        gen.value( "controller", value.getController() );

        if ( value.hasConfig() )
        {
            gen.map( "config" );
            new PropertyTreeMapper( value.getConfig() ).serialize( gen );
            gen.end();
        }
        if ( value.hasRegions() )
        {
            serializeRegions( gen, value.getRegions() );
        }
    }

    private static void serializeRegions( final MapGenerator gen, final PageRegions values )
    {
        gen.map( "regions" );
        if ( values != null )
        {
            for ( final Region region : values )
            {
                new RegionMapper( region ).serialize( gen );
            }
        }
        gen.end();
    }
}
