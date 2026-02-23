package com.enonic.xp.lib.io;

import java.util.function.Function;

import com.google.common.io.ByteSource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ResourceMapper
    implements MapSerializable
{
    private final Resource resource;

    public ResourceMapper( final Resource resource )
    {
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "getSize", (Function<?, Double>) _ -> (double) resource.getSize() );
        gen.value( "getTimestamp", (Function<?, Double>) _ -> (double) resource.getTimestamp() );
        gen.value( "getStream", (Function<?, ByteSource>) _ -> resource.getBytes() );
        gen.value( "exists", (Function<?, Boolean>) _ -> resource.exists() );
    }
}
