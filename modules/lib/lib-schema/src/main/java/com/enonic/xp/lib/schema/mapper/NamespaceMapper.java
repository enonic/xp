package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NamespaceMapper
    implements MapSerializable
{
    private final ApplicationKey key;

    private final String description;

    public NamespaceMapper( final ApplicationKey key, final String description )
    {
        this.key = key;
        this.description = description;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", key.toString() );
        gen.value( "description", description );
    }
}
