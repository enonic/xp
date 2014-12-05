package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

final class RegionMapper
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
        gen.value( "name", this.value.getName() );
        serializeComponents( gen );
    }

    private void serializeComponents( final MapGenerator gen )
    {
        gen.array( "components" );
        for ( final PageComponent component : this.value.getComponents() )
        {
            gen.value( ResultMappers.mapper( component ) );
        }
        gen.end();
    }
}
