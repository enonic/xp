package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Region;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

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

    private void serialize( final MapGenerator gen, final Page value )
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
        if ( value.isFragment() )
        {
            serializeFragment( gen, value.getFragment() );
        }
    }

    private void serializeFragment( final MapGenerator gen, final Component value )
    {
        gen.map( "fragment" );
        new ComponentMapper( value ).serialize( gen );
        gen.end();
    }

    private void serializeRegions( final MapGenerator gen, final PageRegions values )
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
