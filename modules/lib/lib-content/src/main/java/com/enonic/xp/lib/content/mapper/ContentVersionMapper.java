package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ContentVersionMapper
    implements MapSerializable
{
    private final ContentVersion value;

    public ContentVersionMapper( final ContentVersion value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final ContentVersion value )
    {
        gen.value( "id", value.getId() );
        gen.value( "displayName", value.getDisplayName() );
        gen.value( "modifiedTime", value.getModified() );
        gen.value( "modifier", value.getModifier() );
    }
}
