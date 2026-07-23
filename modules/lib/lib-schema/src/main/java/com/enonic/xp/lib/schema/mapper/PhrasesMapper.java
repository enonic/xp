package com.enonic.xp.lib.schema.mapper;

import java.time.Instant;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PhrasesMapper
    implements MapSerializable
{
    private static final String PROPERTIES_EXTENSION = ".properties";

    private final Resource resource;

    public PhrasesMapper( final Resource resource )
    {
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "application", resource.getKey().getApplicationKey() );
        gen.value( "name", stripExtension( resource.getKey().getName() ) );
        gen.value( "modifiedTime", Instant.ofEpochMilli( resource.getTimestamp() ) );
        gen.value( "resource", resource.readString() );
    }

    private static String stripExtension( final String fileName )
    {
        return fileName.endsWith( PROPERTIES_EXTENSION )
            ? fileName.substring( 0, fileName.length() - PROPERTIES_EXTENSION.length() )
            : fileName;
    }
}
