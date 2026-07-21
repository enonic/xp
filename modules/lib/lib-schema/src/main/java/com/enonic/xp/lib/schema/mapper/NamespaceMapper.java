package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.app.Namespace;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NamespaceMapper
    implements MapSerializable
{
    private final Namespace namespace;

    public NamespaceMapper( final Namespace namespace )
    {
        this.namespace = namespace;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", namespace.getKey().toString() );
        gen.value( "description", namespace.getDescription() );
    }
}
