package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class IconMapper
    implements MapSerializable
{
    private final Icon icon;

    public IconMapper( final Icon icon )
    {
        this.icon = icon;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "data", new IconByteSource( icon ) );
        gen.value( "mimeType", icon.getMimeType() );
        gen.value( "modifiedTime", icon.getModifiedTime() );
    }
}
