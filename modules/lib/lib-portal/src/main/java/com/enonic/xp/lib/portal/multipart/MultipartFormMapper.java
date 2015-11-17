package com.enonic.xp.lib.portal.multipart;

import java.util.Collections;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

public final class MultipartFormMapper
    implements MapSerializable
{
    private final MultipartForm form;

    public MultipartFormMapper( final MultipartForm form )
    {
        this.form = form;
    }

    private Iterable<MultipartItem> getItems()
    {
        return this.form != null ? this.form : Collections.emptyList();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( final MultipartItem item : getItems() )
        {
            serialize( gen, item );
        }
    }

    private void serialize( final MapGenerator gen, final MultipartItem item )
    {
        gen.map( item.getName() );
        gen.value( "name", item.getName() );
        gen.value( "fileName", item.getFileName() );
        gen.value( "contentType", item.getContentType() );
        gen.value( "size", item.getSize() );
        gen.end();
    }
}
