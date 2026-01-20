package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.UpdateContentMetadataResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class UpdateContentMetadataResultMapper
    implements MapSerializable
{
    private final UpdateContentMetadataResult value;

    public UpdateContentMetadataResultMapper( final UpdateContentMetadataResult value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "content", new ContentMapper( value.getContent() ) );
    }
}
