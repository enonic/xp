package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

final class PageMapper
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
        gen.value( "template", this.value.getTemplate() );
        gen.value( "controller", this.value.getController() );
        gen.value( "config", ResultMappers.mapper( this.value.getConfig() ) );

        serializeRegions( gen );
    }

    private void serializeRegions( final MapGenerator gen )
    {
        gen.array( "regions" );
        for ( final Region region : this.value.getRegions() )
        {
            gen.value( ResultMappers.mapper( region ) );
        }
        gen.end();
    }
}
