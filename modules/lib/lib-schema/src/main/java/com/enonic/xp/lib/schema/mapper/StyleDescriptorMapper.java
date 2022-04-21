package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.style.StyleDescriptor;

public class StyleDescriptorMapper
    implements MapSerializable
{
    private final StyleDescriptor descriptor;

    private final ApplicationKey applicationKey;

    private final String resource;

    public StyleDescriptorMapper( final StyleDescriptor descriptor, final ApplicationKey applicationKey, final String resource )
    {
        this.descriptor = descriptor;
        this.applicationKey = applicationKey;
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", applicationKey );
        gen.value( "resource", resource );
    }
}
