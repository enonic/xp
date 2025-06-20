package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.Content;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class SiteMapper
    implements MapSerializable
{
    private final Content site;

    public SiteMapper( final Content site )
    {
        this.site = site;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        new ContentMapper( this.site ).serialize( gen );
        gen.value( "description", this.site.getData().getString( "description" ) );
    }
}
