package com.enonic.xp.lib.portal.multipart;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.multipart.MultipartItem;

public final class MultipartItemMapper
    implements MapSerializable
{
    private final MultipartItem item;

    public MultipartItemMapper( final MultipartItem item )
    {
        this.item = item;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, item );
    }

    public static void serialize( final MapGenerator gen, final MultipartItem item )
    {
        gen.map( item.getName() );
        gen.value( "name", item.getName() );
        gen.value( "fileName", item.getFileName() );
        gen.value( "contentType", item.getContentType() );
        gen.value( "size", item.getSize() );
        gen.end();
    }
}
